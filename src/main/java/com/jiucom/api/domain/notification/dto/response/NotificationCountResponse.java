package com.jiucom.api.domain.notification.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationCountResponse {

    private long unreadCount;

    public static NotificationCountResponse of(long unreadCount) {
        return NotificationCountResponse.builder()
                .unreadCount(unreadCount)
                .build();
    }
}
