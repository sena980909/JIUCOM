package com.jiucom.api.domain.review.dto.response;

import com.jiucom.api.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {

    private Long id;
    private Long partId;
    private String partName;
    private Long authorId;
    private String authorNickname;
    private int rating;
    private String content;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .partId(review.getPart().getId())
                .partName(review.getPart().getName())
                .authorId(review.getAuthor().getId())
                .authorNickname(review.getAuthor().getNickname())
                .rating(review.getRating())
                .content(review.getContent())
                .likeCount(review.getLikeCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
