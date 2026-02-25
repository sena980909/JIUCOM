package com.jiucom.api.domain.comment.controller;

import com.jiucom.api.domain.comment.service.CommentService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "댓글 API")
@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // TODO: implement
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "댓글 작성")
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createComment(@PathVariable Long postId) {
        // TODO: implement
        return ResponseEntity.ok(ApiResponse.created(null));
    }
}
