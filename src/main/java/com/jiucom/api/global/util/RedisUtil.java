package com.jiucom.api.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiucom.api.domain.comment.dto.response.CommentListResponse;
import com.jiucom.api.domain.part.dto.response.PartDetailResponse;
import com.jiucom.api.domain.post.dto.response.CachedPostListResponse;
import com.jiucom.api.domain.post.dto.response.PostDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PRICE_CACHE_PREFIX = "price:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String POST_DETAIL_PREFIX = "post:detail:";
    private static final String POST_LIST_PREFIX = "post:list:";
    private static final String PART_DETAIL_PREFIX = "part:detail:";
    private static final String PART_CATEGORIES_KEY = "part:categories";
    private static final String COMMENT_LIST_PREFIX = "comment:list:";

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.warn("Redis deleteByPattern 실패 (pattern: {}): {}", pattern, e.getMessage());
        }
    }

    // Refresh Token
    public void saveRefreshToken(Long userId, String token, long expirationMs) {
        set(REFRESH_TOKEN_PREFIX + userId, token, expirationMs, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(Long userId) {
        Object value = get(REFRESH_TOKEN_PREFIX + userId);
        return value != null ? value.toString() : null;
    }

    public void deleteRefreshToken(Long userId) {
        delete(REFRESH_TOKEN_PREFIX + userId);
    }

    // Price Cache
    public void cachePriceData(Long partId, Object priceData, long ttlMinutes) {
        set(PRICE_CACHE_PREFIX + partId, priceData, ttlMinutes, TimeUnit.MINUTES);
    }

    public Object getCachedPriceData(Long partId) {
        return get(PRICE_CACHE_PREFIX + partId);
    }

    public void evictPriceCache(Long partId) {
        delete(PRICE_CACHE_PREFIX + partId);
    }

    // ===== Post Cache =====

    public void cachePostDetail(Long postId, PostDetailResponse data) {
        try {
            set(POST_DETAIL_PREFIX + postId, data, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis cachePostDetail 실패 (postId: {}): {}", postId, e.getMessage());
        }
    }

    public PostDetailResponse getCachedPostDetail(Long postId) {
        try {
            Object value = get(POST_DETAIL_PREFIX + postId);
            if (value == null) return null;
            return objectMapper.convertValue(value, PostDetailResponse.class);
        } catch (Exception e) {
            log.warn("Redis getCachedPostDetail 실패 (postId: {}): {}", postId, e.getMessage());
            return null;
        }
    }

    public void evictPostDetail(Long postId) {
        try {
            delete(POST_DETAIL_PREFIX + postId);
        } catch (Exception e) {
            log.warn("Redis evictPostDetail 실패 (postId: {}): {}", postId, e.getMessage());
        }
    }

    public void cachePostList(String boardType, int page, int size, CachedPostListResponse data) {
        try {
            String key = POST_LIST_PREFIX + (boardType != null ? boardType : "ALL") + ":" + page + ":" + size;
            set(key, data, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis cachePostList 실패: {}", e.getMessage());
        }
    }

    public CachedPostListResponse getCachedPostList(String boardType, int page, int size) {
        try {
            String key = POST_LIST_PREFIX + (boardType != null ? boardType : "ALL") + ":" + page + ":" + size;
            Object value = get(key);
            if (value == null) return null;
            return objectMapper.convertValue(value, CachedPostListResponse.class);
        } catch (Exception e) {
            log.warn("Redis getCachedPostList 실패: {}", e.getMessage());
            return null;
        }
    }

    public void evictAllPostLists() {
        try {
            deleteByPattern(POST_LIST_PREFIX + "*");
        } catch (Exception e) {
            log.warn("Redis evictAllPostLists 실패: {}", e.getMessage());
        }
    }

    // ===== Part Cache =====

    public void cachePartDetail(Long partId, PartDetailResponse data) {
        try {
            set(PART_DETAIL_PREFIX + partId, data, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis cachePartDetail 실패 (partId: {}): {}", partId, e.getMessage());
        }
    }

    public PartDetailResponse getCachedPartDetail(Long partId) {
        try {
            Object value = get(PART_DETAIL_PREFIX + partId);
            if (value == null) return null;
            return objectMapper.convertValue(value, PartDetailResponse.class);
        } catch (Exception e) {
            log.warn("Redis getCachedPartDetail 실패 (partId: {}): {}", partId, e.getMessage());
            return null;
        }
    }

    public void evictPartDetail(Long partId) {
        try {
            delete(PART_DETAIL_PREFIX + partId);
        } catch (Exception e) {
            log.warn("Redis evictPartDetail 실패 (partId: {}): {}", partId, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getCachedPartCategories() {
        try {
            Object value = get(PART_CATEGORIES_KEY);
            if (value == null) return null;
            return objectMapper.convertValue(value, List.class);
        } catch (Exception e) {
            log.warn("Redis getCachedPartCategories 실패: {}", e.getMessage());
            return null;
        }
    }

    public void cachePartCategories(List<String> categories) {
        try {
            set(PART_CATEGORIES_KEY, categories, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("Redis cachePartCategories 실패: {}", e.getMessage());
        }
    }

    // ===== Comment Cache =====

    public void cacheCommentList(Long postId, int page, int size, CommentListResponse data) {
        try {
            String key = COMMENT_LIST_PREFIX + postId + ":" + page + ":" + size;
            set(key, data, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis cacheCommentList 실패: {}", e.getMessage());
        }
    }

    public CommentListResponse getCachedCommentList(Long postId, int page, int size) {
        try {
            String key = COMMENT_LIST_PREFIX + postId + ":" + page + ":" + size;
            Object value = get(key);
            if (value == null) return null;
            return objectMapper.convertValue(value, CommentListResponse.class);
        } catch (Exception e) {
            log.warn("Redis getCachedCommentList 실패: {}", e.getMessage());
            return null;
        }
    }

    public void evictCommentListsForPost(Long postId) {
        try {
            deleteByPattern(COMMENT_LIST_PREFIX + postId + ":*");
        } catch (Exception e) {
            log.warn("Redis evictCommentListsForPost 실패 (postId: {}): {}", postId, e.getMessage());
        }
    }
}
