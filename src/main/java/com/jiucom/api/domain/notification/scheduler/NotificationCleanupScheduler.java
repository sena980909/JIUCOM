package com.jiucom.api.domain.notification.scheduler;

import com.jiucom.api.domain.notification.entity.Notification;
import com.jiucom.api.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 3 * * *") // Every day at 3 AM
    @Transactional
    public void cleanupOldNotifications() {
        log.info("Starting notification cleanup job...");

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Notification> oldReadNotifications =
                notificationRepository.findByIsReadTrueAndIsDeletedFalseAndCreatedAtBefore(thirtyDaysAgo);

        int count = 0;
        for (Notification notification : oldReadNotifications) {
            notification.softDelete();
            count++;
        }

        log.info("Notification cleanup job completed. Soft-deleted {} old read notifications.", count);
    }
}
