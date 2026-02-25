package com.jiucom.api.domain.post.controller;

import com.jiucom.api.domain.post.dto.request.PostCreateRequest;
import com.jiucom.api.domain.post.dto.request.PostUpdateRequest;
import com.jiucom.api.domain.post.dto.response.PostDetailResponse;
import com.jiucom.api.domain.post.dto.response.PostListResponse;
import com.jiucom.api.domain.post.service.PostService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post", description = "게시글 API")
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getPosts(
            @RequestParam(required = false) String boardType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PostListResponse> posts = postService.getPosts(boardType, page, size);
        return ResponseEntity.ok(ApiResponse.ok(posts));
    }

    @Operation(summary = "게시글 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PostListResponse> posts = postService.searchPosts(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.ok(posts));
    }

    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(@PathVariable Long postId) {
        PostDetailResponse post = postService.getPostDetail(postId);
        return ResponseEntity.ok(ApiResponse.ok(post));
    }

    @Operation(summary = "게시글 작성")
    @PostMapping
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPost(
            @Valid @RequestBody PostCreateRequest request) {
        PostDetailResponse post = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(post));
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request) {
        PostDetailResponse post = postService.updatePost(postId, request);
        return ResponseEntity.ok(ApiResponse.ok(post));
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
