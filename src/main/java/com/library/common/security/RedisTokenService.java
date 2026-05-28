package com.library.common.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenService {
    private static final String TOKEN_PREFIX = "library:token:";

    private final StringRedisTemplate stringRedisTemplate;

    public RedisTokenService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void saveToken(String token, Long userId, long expirationMillis) {
        stringRedisTemplate.opsForValue().set(TOKEN_PREFIX + token, String.valueOf(userId), expirationMillis, TimeUnit.MILLISECONDS);
    }

    public boolean exists(String token) {
        Boolean exists = stringRedisTemplate.hasKey(TOKEN_PREFIX + token);
        return Boolean.TRUE.equals(exists);
    }

    public void delete(String token) {
        stringRedisTemplate.delete(TOKEN_PREFIX + token);
    }
}
