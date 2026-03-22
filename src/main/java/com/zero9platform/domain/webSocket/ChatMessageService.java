package com.zero9platform.domain.webSocket;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.common.util.SearchProfanityFilter;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SearchProfanityFilter searchProfanityFilter;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private final Long ADMIN_ID = 1L;

    /**
     * 회원 문의하기
     * 회원 → 관리자 문의
     */
    @Transactional
    public void sendToAdmin(Long senderId, String message) {

        if (searchProfanityFilter.isBadWord(message)) {
            log.warn("비속어 필터링에 걸린 검색어: [{}]", message);

            throw new CustomException(ExceptionCode.CHAT_BAD_WORD_DETECTED, message);
        }

        ChatRoom room = getOrCreateRoom(senderId);

        // 1. 메세지 entity 생성
        ChatMessage chatMessage = new ChatMessage(room.getId(), senderId, ADMIN_ID, message);

        // 2. DB저장
        chatMessageRepository.save(chatMessage);

        room.updateLastMessage(message);
        chatRoomRepository.save(room);

        // 3. WebSocket 전달
        simpMessagingTemplate.convertAndSend(
                "/queue/chat/" + ADMIN_ID,
                chatMessage
        );

        sendAdminUnreadCount();
    }

    /**
     * 관리자 답변하기
     * 관리자 → 회원 답변
     */
    @Transactional
    public void sendToUser(Long userId, String message) {

        if (searchProfanityFilter.isBadWord(message)) {
            log.warn("비속어 필터링에 걸린 검색어: [{}]", message);

            throw new CustomException(ExceptionCode.CHAT_BAD_WORD_DETECTED, message);
        }

        ChatRoom room = getOrCreateRoom(userId);

        // 1. 메세지 entity 생성
        ChatMessage chatMessage = new ChatMessage(room.getId(), ADMIN_ID, userId, message);

        // 2. DB저장
        chatMessageRepository.save(chatMessage);

        room.updateLastMessage(message);
        chatRoomRepository.save(room);

        // 3. 회원 온라인 여부 확인
        if (checkOnline(userId).getOnlineStatus()) {

            // 4. WebSocket 전달
            simpMessagingTemplate.convertAndSend(
                    "/queue/chat/" + userId,
                    chatMessage
            );
        }
        sendUserUnreadCount(room);
    }

    /**
     * 채팅 기록 조회
     */
    @Transactional
    public PageResponse<ChatMessage> getChatHistory(Long userId, Pageable pageable) {

        ChatRoom room = chatRoomRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_ROOM_NOT_FOUND));

        List<ChatMessage> unreadMessages = chatMessageRepository.findByRoomIdAndReceiverIdAndIsReadFalse(room.getId(), userId);

        unreadMessages.forEach(ChatMessage::markAsRead);

        Page<ChatMessage> page = chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(room.getId(), pageable);

        sendUserUnreadCount(room);

        return PageResponse.from(page);
    }

    /**
     * 관리자 문의 목록
     */
    @Transactional
    public PageResponse<ChatRoomPreviewResponse> getAdminInquiries(Pageable pageable) {

        Page<ChatRoom> page = chatRoomRepository.findAllByAdminIdOrderByLastMessageTimeDesc(ADMIN_ID, pageable);

        Page<ChatRoomPreviewResponse> responsePage =
                page.map(room -> new ChatRoomPreviewResponse(
                        room.getId(),
                        room.getUserId(),
                        room.getLastMessage(),
                        room.getLastMessageTime()
                ));

        return PageResponse.from(responsePage);
    }

    /**
     * 읽지 않은 메시지 수
     */
    @Transactional
    public long getUnreadCount(Long userId) {

        ChatRoom room = chatRoomRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CHAT_ROOM_NOT_FOUND));

        return chatMessageRepository.countByRoomIdAndReceiverIdAndIsReadFalse(room.getId(), userId);
    }

    /**
     * 온라인 여부 확인
     */
    @Transactional
    public OnlineCheckResponse checkOnline(Long userId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_WITHDRAWN));

        Boolean online = redisTemplate.opsForSet()
                        .isMember("online_users", userId.toString());

        return OnlineCheckResponse.from(user, Boolean.TRUE.equals(online));
    }

    /**
     * 전체 온라인 유저 조회
     */
    @Transactional
    public Set<String> getAllOnlineUsers() {

        return redisTemplate.opsForSet().members("online_users");
    }

    /**
     * 채팅방 조회 / 생성
     */
    private ChatRoom getOrCreateRoom(Long userId) {

        return chatRoomRepository.findByUserId(userId)
                .orElseGet(() ->
                        chatRoomRepository.save(
                                new ChatRoom(userId, ADMIN_ID)
                        )
                );
    }

    /**
     * 관리자 unread count
     */
    private void sendAdminUnreadCount() {

        long unreadCount =
                chatMessageRepository.countByReceiverIdAndIsReadFalse(ADMIN_ID);

        pushUnreadCount(ADMIN_ID, unreadCount);
    }

    /**
     * 회원 unread count
     */
    private void sendUserUnreadCount(ChatRoom room) {

        long unreadCount =
                chatMessageRepository.countByRoomIdAndReceiverIdAndIsReadFalse(
                        room.getId(),
                        room.getUserId()
                );

        pushUnreadCount(room.getUserId(), unreadCount);
    }

    /**
     * unread push
     */
    private void pushUnreadCount(Long userId, long unreadCount) {

        simpMessagingTemplate.convertAndSend(
                "/queue/unread/" + userId,
                unreadCount
        );
    }
}
