package com.jiucom.api.domain.build.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jiucom.api.domain.build.entity.Build;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BuildListResponse {

    private Long id;
    private String name;
    private String description;
    private Integer totalPrice;
    private int partCount;
    @JsonProperty("isPublic")
    private boolean isPublic;
    private int viewCount;
    private int likeCount;
    private String ownerNickname;
    private LocalDateTime createdAt;

    public static BuildListResponse from(Build build) {
        return BuildListResponse.builder()
                .id(build.getId())
                .name(build.getName())
                .description(build.getDescription())
                .totalPrice(build.getTotalPrice())
                .partCount(build.getBuildParts().size())
                .isPublic(build.isPublic())
                .viewCount(build.getViewCount())
                .likeCount(build.getLikeCount())
                .ownerNickname(build.getUser().getNickname())
                .createdAt(build.getCreatedAt())
                .build();
    }
}
