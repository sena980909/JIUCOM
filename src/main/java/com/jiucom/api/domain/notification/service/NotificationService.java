package com.jiucom.api.domain.notification.service;

import com.jiucom.api.domain.notification.dto.response.NotificationCountResponse;
import com.jiucom.api.domain.notification.dto.response.NotificationResponse;
import com.jiucom.api.domain.notification.entity.Notification;
import com.jiucom.api.domain.notification.entity.enums.NotificationType;
import com.jiucom.api.domain.notification.repository.NotificationRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Page<NotificationResponse> getNotifications(int page, int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationResponse::from);
    }

    public NotificationCountResponse getUnreadCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        long count = notificationRepository.countByUserIdAndIsReadFalseAndIsDeletedFalse(userId);
        return NotificationCountResponse.of(count);
    }

    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> !n.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUser().getId().equals(userId)) {
            throw new GlobalException(GlobalErrorCode.FORBIDDEN);
        }

        notification.markAsRead();
        return NotificationResponse.from(notification);
    }

    @Transactional
    public void markAllAsRead() {
        Long userId = SecurityUtil.getCurrentUserId();
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Transactional
    public void sendNotification(Long userId, NotificationType type, String title, String message, String linkUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .linkUrl(linkUrl)
                .build();

        notificationRepository.save(notification);

        // Send via WebSocket
        try {
            NotificationResponse response = NotificationResponse.from(notification);
            messagingTemplate.convertAndSend("/queue/notifications/" + userId, response);
        } catch (Exception e) {
            log.warn("Failed to send WebSocket notification to user {}: {}", userId, e.getMessage());
        }
    }
}
