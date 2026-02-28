package com.jiucom.api.domain.part.dto.response;

import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.price.entity.PriceEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartDetailResponse {

    private Long id;
    private String name;
    private String category;
    private String manufacturer;
    private String modelNumber;
    private String imageUrl;
    private String specs;
    private Integer lowestPrice;
    private Integer highestPrice;
    private List<PriceEntryResponse> prices;

    public static PartDetailResponse of(Part part, List<PriceEntry> priceEntries) {
        return PartDetailResponse.builder()
                .id(part.getId())
                .name(part.getName())
                .category(part.getCategory().name())
                .manufacturer(part.getManufacturer())
                .modelNumber(part.getModelNumber())
                .imageUrl(part.getImageUrl())
                .specs(part.getSpecs())
                .lowestPrice(part.getLowestPrice())
                .highestPrice(part.getHighestPrice())
                .prices(priceEntries.stream().map(PriceEntryResponse::from).toList())
                .build();
    }
}
