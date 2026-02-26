package com.jiucom.api.domain.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SearchResultResponse {

    private List<PartResult> parts;
    private List<PostResult> posts;
    private List<BuildResult> builds;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PartResult {
        private Long id;
        private String name;
        private String category;
        private String manufacturer;
        private Integer lowestPrice;
        private String imageUrl;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PostResult {
        private Long id;
        private String title;
        private String boardType;
        private String authorNickname;
        private int viewCount;
        private int likeCount;
        private int commentCount;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class BuildResult {
        private Long id;
        private String name;
        private String description;
        private Integer totalPrice;
        private int likeCount;
        private String userNickname;
    }
}
