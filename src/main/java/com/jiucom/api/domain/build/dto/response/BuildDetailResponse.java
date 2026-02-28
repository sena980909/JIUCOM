package com.jiucom.api.domain.build.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jiucom.api.domain.build.entity.Build;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BuildDetailResponse {

    private Long id;
    private String name;
    private String description;
    private Integer totalPrice;
    @JsonProperty("isPublic")
    private boolean isPublic;
    private int viewCount;
    private int likeCount;
    private String ownerNickname;
    private Long ownerId;
    private List<BuildPartResponse> parts;
    private List<String> compatibilityWarnings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BuildDetailResponse from(Build build, List<String> warnings) {
        List<BuildPartResponse> partResponses = build.getBuildParts().stream()
                .map(BuildPartResponse::from)
                .toList();

        return BuildDetailResponse.builder()
                .id(build.getId())
                .name(build.getName())
                .description(build.getDescription())
                .totalPrice(build.getTotalPrice())
                .isPublic(build.isPublic())
                .viewCount(build.getViewCount())
                .likeCount(build.getLikeCount())
                .ownerNickname(build.getUser().getNickname())
                .ownerId(build.getUser().getId())
                .parts(partResponses)
                .compatibilityWarnings(warnings)
                .createdAt(build.getCreatedAt())
                .updatedAt(build.getUpdatedAt())
                .build();
    }
}
