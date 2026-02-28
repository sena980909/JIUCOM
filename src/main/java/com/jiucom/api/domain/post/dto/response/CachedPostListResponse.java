package com.jiucom.api.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CachedPostListResponse {

    private List<PostListResponse> content;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int size;
}
