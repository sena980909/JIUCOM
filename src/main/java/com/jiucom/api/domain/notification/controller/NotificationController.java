package com.jiucom.api.domain.notification.controller;

import com.jiucom.api.domain.notification.dto.response.NotificationCountResponse;
import com.jiucom.api.domain.notification.dto.response.NotificationResponse;
import com.jiucom.api.domain.notification.service.NotificationService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<NotificationResponse> notifications = notificationService.getNotifications(page, size);
        return ResponseEntity.ok(ApiResponse.ok(notifications));
    }

    @Operation(summary = "읽지 않은 알림 수")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<NotificationCountResponse>> getUnreadCount() {
        NotificationCountResponse count = notificationService.getUnreadCount();
        return ResponseEntity.ok(ApiResponse.ok(count));
    }

    @Operation(summary = "알림 읽음 처리")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Long notificationId) {
        NotificationResponse notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.ok(notification));
    }

    @Operation(summary = "전체 알림 읽음 처리")
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
