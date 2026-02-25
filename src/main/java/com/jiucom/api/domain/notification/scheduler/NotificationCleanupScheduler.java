package com.jiucom.api.domain.notification.scheduler;

import com.jiucom.api.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 3 * * *") // Every day at 3 AM
    public void cleanupOldNotifications() {
        log.info("Starting notification cleanup job...");
        // TODO: implement cleanup of read notifications older than 30 days
        log.info("Notification cleanup job completed.");
    }
}
