package com.jiucom.api.domain.comment.controller;

import com.jiucom.api.domain.comment.dto.request.CommentCreateRequest;
import com.jiucom.api.domain.comment.dto.request.CommentUpdateRequest;
import com.jiucom.api.domain.comment.dto.response.CommentListResponse;
import com.jiucom.api.domain.comment.dto.response.CommentResponse;
import com.jiucom.api.domain.comment.service.CommentService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "댓글 API")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 목록 조회")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentListResponse>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        CommentListResponse comments = commentService.getComments(postId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(comments));
    }

    @Operation(summary = "댓글 작성")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request) {
        CommentResponse comment = commentService.createComment(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(comment));
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request) {
        CommentResponse comment = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(ApiResponse.ok(comment));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
