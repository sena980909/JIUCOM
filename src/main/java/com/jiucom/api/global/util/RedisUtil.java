package com.jiucom.api.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PRICE_CACHE_PREFIX = "price:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

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
}
