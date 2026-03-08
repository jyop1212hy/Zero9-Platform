package com.zero9platform.domain.notification;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.gpp_follow.repository.FollowRepository;
import com.zero9platform.domain.notification.entity.Notification;
import com.zero9platform.domain.notification.sse.SseEmitterManager;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final SseEmitterManager sseEmitterManager;
    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(GroupPurchasePostStatusChangedEvent event) {

        // 1. 팔로우 유저 조회
        List<Long> userIds =
                followRepository.findUserIdsByGroupPurchasePostId(event.getGroupPurchasePostId());

        // [로그 추가] 실제로 어떤 유저 ID들이 뽑히는지 확인
        log.info("알림 발송 대상자 리스트 (GPP ID: {}): {}", event.getGroupPurchasePostId(), userIds);

        if (userIds == null || userIds.isEmpty()) {
            log.warn("알림을 받을 팔로워가 없습니다.");
            return;
        }

        // 2. 알림 메시지 생성
        String title = "공동구매 상태 변경";

        String content = String.format(
                "[%s] 상태가 %s → %s 로 변경되었습니다.",
                event.getGroupPurchasePostTitle(),
                event.getOldStatus(),
                event.getNewStatus()
        );

        // 3. 알림 생성
        List<Notification> notifications = userIds.stream()
                .map(userId -> new Notification(
                        userId,
                        event.getGroupPurchasePostId(),
                        title,
                        content
                ))
                .toList();

        // 4. DB 저장
        notificationRepository.saveAll(notifications);

        // 5. SSE 전송
        notifications.forEach(notification ->
                sseEmitterManager.send(notification.getUserId(), notification)
        );
    }

    /**
     * 내 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotifications(Long userId) {

        // 1. User 조회 (AuthUser)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 2. 회원 탈퇴 유무 확인
        if(user.getDeletedAt() != null) {
            throw new CustomException(ExceptionCode.USER_WITHDRAWN);
        }

        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOTIFICATION_NOT_FOUND));

        // 본인 알림만 읽음 처리 가능
        if (!notification.getUserId().equals(userId)) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }

        notification.markAsRead();
    }
}