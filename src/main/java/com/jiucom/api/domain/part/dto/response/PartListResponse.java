package com.jiucom.api.domain.part.dto.response;

import com.jiucom.api.domain.part.entity.Part;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartListResponse {

    private Long id;
    private String name;
    private String category;
    private String manufacturer;
    private String imageUrl;
    private Integer lowestPrice;
    private Integer highestPrice;

    public static PartListResponse from(Part part) {
        return PartListResponse.builder()
                .id(part.getId())
                .name(part.getName())
                .category(part.getCategory().name())
                .manufacturer(part.getManufacturer())
                .imageUrl(part.getImageUrl())
                .lowestPrice(part.getLowestPrice())
                .highestPrice(part.getHighestPrice())
                .build();
    }
}
