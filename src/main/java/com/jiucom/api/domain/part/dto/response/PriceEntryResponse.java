package com.jiucom.api.domain.part.dto.response;

import com.jiucom.api.domain.price.entity.PriceEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceEntryResponse {

    private Long id;
    private String sellerName;
    private String sellerUrl;
    private Integer price;
    private String productUrl;
    private boolean available;

    public static PriceEntryResponse from(PriceEntry entry) {
        return PriceEntryResponse.builder()
                .id(entry.getId())
                .sellerName(entry.getSeller().getName())
                .sellerUrl(entry.getSeller().getSiteUrl())
                .price(entry.getPrice())
                .productUrl(entry.getProductUrl())
                .available(entry.isAvailable())
                .build();
    }
}
