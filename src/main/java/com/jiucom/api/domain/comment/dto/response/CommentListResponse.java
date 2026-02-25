package com.jiucom.api.domain.comment.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CommentListResponse {

    private List<CommentResponse> comments;
    private int totalPages;
    private long totalElements;
    private int currentPage;
}
