package com.jiucom.api.domain.seller.dto.response;

import com.jiucom.api.domain.seller.entity.Seller;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SellerResponse {

    private Long id;
    private String name;
    private String siteUrl;
    private String logoUrl;
    private String status;
    private double reliabilityScore;

    public static SellerResponse from(Seller seller) {
        return SellerResponse.builder()
                .id(seller.getId())
                .name(seller.getName())
                .siteUrl(seller.getSiteUrl())
                .logoUrl(seller.getLogoUrl())
                .status(seller.getStatus().name())
                .reliabilityScore(seller.getReliabilityScore())
                .build();
    }
}
