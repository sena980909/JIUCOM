package com.jiucom.api.domain.post.service;

import com.jiucom.api.domain.post.dto.request.PostCreateRequest;
import com.jiucom.api.domain.post.dto.request.PostUpdateRequest;
import com.jiucom.api.domain.post.dto.response.PostDetailResponse;
import com.jiucom.api.domain.post.dto.response.PostListResponse;
import com.jiucom.api.domain.post.entity.Post;
import com.jiucom.api.domain.post.entity.enums.BoardType;
import com.jiucom.api.domain.post.repository.PostRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Page<PostListResponse> getPosts(String boardType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> posts;
        if (boardType != null && !boardType.isBlank()) {
            BoardType type = BoardType.valueOf(boardType.toUpperCase());
            posts = postRepository.findByBoardTypeAndIsDeletedFalse(type, pageable);
        } else {
            posts = postRepository.findByIsDeletedFalse(pageable);
        }

        return posts.map(PostListResponse::from);
    }

    public Page<PostListResponse> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalse(keyword, pageable)
                .map(PostListResponse::from);
    }

    @Transactional
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));
        post.incrementViewCount();
        return PostDetailResponse.from(post);
    }

    @Transactional
    public PostDetailResponse createPost(PostCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        Post post = Post.builder()
                .author(user)
                .boardType(request.getBoardType())
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        postRepository.save(post);
        return PostDetailResponse.from(post);
    }

    @Transactional
    public PostDetailResponse updatePost(Long postId, PostUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        Post post = postRepository.findById(postId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new GlobalException(GlobalErrorCode.POST_NOT_AUTHOR);
        }

        if (request.getTitle() != null) {
            post.updateTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.updateContent(request.getContent());
        }
        if (request.getBoardType() != null) {
            post.updateBoardType(request.getBoardType());
        }

        return PostDetailResponse.from(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Post post = postRepository.findById(postId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new GlobalException(GlobalErrorCode.POST_NOT_AUTHOR);
        }

        post.softDelete();
    }
}
