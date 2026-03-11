package com.zero9platform.domain.notification;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 내 알림 목록 조회
     */
    @GetMapping
    public ResponseEntity<CommonResponse<List<Notification>>> getNotifications(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        List<Notification> notifications = notificationService.getNotifications(authUser.getId());
        return ResponseEntity.ok(CommonResponse.success("알림 목록 조회 성공", notifications));
    }

    /**
     * 알림 읽음 처리
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<CommonResponse<Void>> markAsRead(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(authUser.getId(), notificationId);
        return ResponseEntity.ok(CommonResponse.success("알림 읽음 처리 성공", null));
    }
}
