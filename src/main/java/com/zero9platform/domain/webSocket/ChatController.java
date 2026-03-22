package com.zero9platform.domain.webSocket;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    /**
     * WebSocket 메시지
     */
    @MessageMapping("/chat")
    public void sendMessage(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody ChatMessageRequest request) {

        Long senderId = authUser.getId();
        String message = request.getMessage();

        chatMessageService.sendToAdmin(senderId, message);
    }

    /**
     * 채팅 기록 조회
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<CommonResponse<PageResponse<ChatMessage>>> history(@PathVariable Long userId, Pageable pageable) {

        PageResponse<ChatMessage> history = chatMessageService.getChatHistory(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("채팅 기록 조회 성공", history));
    }

    /**
     * 온라인 상태 단건 조회
     */
    @GetMapping("/online-status/{userId}")
    public ResponseEntity<CommonResponse<OnlineCheckResponse>> status(@PathVariable Long userId) {

        OnlineCheckResponse result = chatMessageService.checkOnline(userId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("온라인 상태 조회", result));
    }

    /**
     * 온라인 상태 목록 조회
     */
    @GetMapping("/online-users")
    public ResponseEntity<CommonResponse<Set<String>>> onlineUsers() {

        Set<String> onlineUsers = chatMessageService.getAllOnlineUsers();

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("온라인 유저 조회 성공",onlineUsers));
    }

    /**
     * 관리자 문의 목록
     */
    @GetMapping("/admin/inquiries")
    public ResponseEntity<CommonResponse<PageResponse<ChatRoomPreviewResponse>>> inquiries(Pageable pageable) {

        PageResponse<ChatRoomPreviewResponse> result = chatMessageService.getAdminInquiries(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("관리자 문의 목록 조회 성공", result));
    }

    /**
     * 읽지 않은 메시지 수
     */
    @GetMapping("/unread/{userId}")
    public ResponseEntity<CommonResponse<Long>> unread(@PathVariable Long userId) {

        long count = chatMessageService.getUnreadCount(userId);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("읽지 않은 메시지 조회 성공", count));
    }
}
