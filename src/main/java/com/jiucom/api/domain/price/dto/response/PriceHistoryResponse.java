package com.jiucom.api.domain.price.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class PriceHistoryResponse {

    private Long partId;
    private String partName;
    private String period;
    private List<DailyPricePoint> history;

    @Getter
    @Builder
    public static class DailyPricePoint {
        private LocalDate date;
        private Integer lowestPrice;
        private Integer highestPrice;
        private Integer averagePrice;
    }
}
