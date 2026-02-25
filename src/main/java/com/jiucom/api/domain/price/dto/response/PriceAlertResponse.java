package com.jiucom.api.domain.price.dto.response;

import com.jiucom.api.domain.price.entity.PriceAlert;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PriceAlertResponse {

    private Long id;
    private Long partId;
    private String partName;
    private Integer targetPrice;
    private Integer currentLowestPrice;
    private boolean isActive;
    private boolean isTriggered;
    private LocalDateTime createdAt;

    public static PriceAlertResponse from(PriceAlert alert, Integer currentLowestPrice) {
        return PriceAlertResponse.builder()
                .id(alert.getId())
                .partId(alert.getPart().getId())
                .partName(alert.getPart().getName())
                .targetPrice(alert.getTargetPrice())
                .currentLowestPrice(currentLowestPrice)
                .isActive(alert.isActive())
                .isTriggered(alert.isTriggered())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
