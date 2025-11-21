package com.cineplus.cineplus.web.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class SessionActivityService {

    // Inactividad en minutos por defecto 5
    @Value("${session.inactivity.minutes:5}")
    private long inactivityMinutes;

    private final StringRedisTemplate redis;

    private static final String KEY_PREFIX = "session:activity:";

    public SessionActivityService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void touch(String username) {
        if (username == null) return;
        String key = KEY_PREFIX + username;
        String now = Long.toString(System.currentTimeMillis());
        // store timestamp and set TTL to twice inactivity window to allow late checks
        redis.opsForValue().set(key, now, Duration.ofMinutes(Math.max(1, inactivityMinutes * 2)));
    }

    public boolean isInactive(String username) {
        if (username == null) return true;
        String key = KEY_PREFIX + username;
        String raw = redis.opsForValue().get(key);
        if (raw == null) return false; // no registro: asumimos activo
        try {
            long last = Long.parseLong(raw);
            long elapsed = System.currentTimeMillis() - last;
            return elapsed > inactivityMinutes * 60_000L;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public void remove(String username) {
        if (username == null) return;
        String key = KEY_PREFIX + username;
        redis.delete(key);
    }
}
