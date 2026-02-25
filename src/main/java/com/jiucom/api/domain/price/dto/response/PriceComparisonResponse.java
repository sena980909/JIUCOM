package com.jiucom.api.domain.price.dto.response;

import com.jiucom.api.domain.price.entity.PriceEntry;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PriceComparisonResponse {

    private Long partId;
    private String partName;
    private Integer lowestPrice;
    private Integer highestPrice;
    private List<PriceEntryItem> prices;

    @Getter
    @Builder
    public static class PriceEntryItem {
        private Long sellerId;
        private String sellerName;
        private String siteUrl;
        private Integer price;
        private String productUrl;
        private boolean isAvailable;

        public static PriceEntryItem from(PriceEntry entry) {
            return PriceEntryItem.builder()
                    .sellerId(entry.getSeller().getId())
                    .sellerName(entry.getSeller().getName())
                    .siteUrl(entry.getSeller().getSiteUrl())
                    .price(entry.getPrice())
                    .productUrl(entry.getProductUrl())
                    .isAvailable(entry.isAvailable())
                    .build();
        }
    }
}
