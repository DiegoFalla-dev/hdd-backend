package com.cineplus.cineplus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisLockService {
    private final StringRedisTemplate redisTemplate;

    public boolean tryLock(String key, String value, long ttlSeconds) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, ttlSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    public void release(String key, String expectedValue) {
        String val = redisTemplate.opsForValue().get(key);
        if (expectedValue.equals(val)) {
            redisTemplate.delete(key);
        }
    }

    // helper to read current value of a lock key
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}