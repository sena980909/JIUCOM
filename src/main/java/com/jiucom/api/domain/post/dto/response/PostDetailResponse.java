package com.jiucom.api.domain.post.dto.response;

import com.jiucom.api.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {

    private Long id;
    private String boardType;
    private String title;
    private String content;
    private Long authorId;
    private String authorNickname;
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostDetailResponse from(Post post) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .boardType(post.getBoardType().name())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getAuthor() != null ? post.getAuthor().getId() : null)
                .authorNickname(post.getAuthor() != null ? post.getAuthor().getNickname() : "(알 수 없음)")
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
