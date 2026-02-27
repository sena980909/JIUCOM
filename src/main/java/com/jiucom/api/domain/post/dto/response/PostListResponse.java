package com.jiucom.api.domain.post.dto.response;

import com.jiucom.api.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostListResponse {

    private Long id;
    private String boardType;
    private String title;
    private String authorNickname;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;

    public static PostListResponse from(Post post) {
        return PostListResponse.builder()
                .id(post.getId())
                .boardType(post.getBoardType().name())
                .title(post.getTitle())
                .authorNickname(post.getAuthor() != null ? post.getAuthor().getNickname() : "(알 수 없음)")
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
