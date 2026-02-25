package com.jiucom.api.domain.favorite.dto.response;

import com.jiucom.api.domain.favorite.entity.Favorite;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteResponse {

    private Long id;
    private Long partId;
    private String partName;
    private String category;
    private Integer lowestPrice;
    private boolean isFavorite;

    public static FavoriteResponse from(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .partId(favorite.getPart().getId())
                .partName(favorite.getPart().getName())
                .category(favorite.getPart().getCategory().name())
                .lowestPrice(favorite.getPart().getLowestPrice())
                .isFavorite(true)
                .build();
    }
}
