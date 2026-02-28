package com.jiucom.api.domain.post.service;

import com.jiucom.api.domain.post.dto.request.PostCreateRequest;
import com.jiucom.api.domain.post.dto.request.PostUpdateRequest;
import com.jiucom.api.domain.post.dto.response.CachedPostListResponse;
import com.jiucom.api.domain.post.dto.response.PostDetailResponse;
import com.jiucom.api.domain.post.dto.response.PostListResponse;
import com.jiucom.api.domain.post.entity.Post;
import com.jiucom.api.domain.post.entity.enums.BoardType;
import com.jiucom.api.domain.post.repository.PostRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.RedisUtil;
import com.jiucom.api.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final RedisUtil redisUtil;

    public Page<PostListResponse> getPosts(String boardType, int page, int size) {
        // Check cache
        CachedPostListResponse cached = redisUtil.getCachedPostList(boardType, page, size);
        if (cached != null) {
            return new PageImpl<>(
                    cached.getContent(),
                    PageRequest.of(cached.getCurrentPage(), cached.getSize()),
                    cached.getTotalElements()
            );
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> posts;
        if (boardType != null && !boardType.isBlank()) {
            BoardType type = BoardType.valueOf(boardType.toUpperCase());
            posts = postRepository.findByBoardTypeAndIsDeletedFalse(type, pageable);
        } else {
            posts = postRepository.findByIsDeletedFalse(pageable);
        }

        Page<PostListResponse> result = posts.map(PostListResponse::from);

        // Store in cache
        CachedPostListResponse cacheData = CachedPostListResponse.builder()
                .content(result.getContent())
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .currentPage(page)
                .size(size)
                .build();
        redisUtil.cachePostList(boardType, page, size, cacheData);

        return result;
    }

    public Page<PostListResponse> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findByTitleContainingIgnoreCaseAndIsDeletedFalse(keyword, pageable)
                .map(PostListResponse::from);
    }

    @Transactional
    public PostDetailResponse getPostDetail(Long postId) {
        // Increment view count via lightweight JPQL (no entity load)
        postRepository.incrementViewCount(postId);

        // Check cache
        PostDetailResponse cached = redisUtil.getCachedPostDetail(postId);
        if (cached != null) {
            // Return cached version with incremented viewCount
            return PostDetailResponse.builder()
                    .id(cached.getId())
                    .boardType(cached.getBoardType())
                    .title(cached.getTitle())
                    .content(cached.getContent())
                    .authorId(cached.getAuthorId())
                    .authorNickname(cached.getAuthorNickname())
                    .viewCount(cached.getViewCount() + 1)
                    .likeCount(cached.getLikeCount())
                    .commentCount(cached.getCommentCount())
                    .createdAt(cached.getCreatedAt())
                    .updatedAt(cached.getUpdatedAt())
                    .build();
        }

        // Cache miss - load from DB
        Post post = postRepository.findById(postId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.POST_NOT_FOUND));

        PostDetailResponse response = PostDetailResponse.from(post);

        // Store in cache
        redisUtil.cachePostDetail(postId, response);

        return response;
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

        // Invalidate list caches
        redisUtil.evictAllPostLists();

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

        // Invalidate caches
        redisUtil.evictPostDetail(postId);
        redisUtil.evictAllPostLists();

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

        // Invalidate caches
        redisUtil.evictPostDetail(postId);
        redisUtil.evictAllPostLists();
    }
}
