package com.cineplus.cineplus.service;

import com.cineplus.cineplus.persistance.entity.Seat;
import com.cineplus.cineplus.persistance.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    private final RedisLockService redisLockService;
    private static final String LOCK_PREFIX = "seat-lock:"; // seat-lock:{showId}:{seatId}

    public List<Seat> listByShow(Long showId) {
        return seatRepository.findByShowId(showId);
    }

    /**
     * Try reserving seats using Redis TTL locks. Returns list of locked keys if success.
     * Throws IllegalStateException if any seat already BOOKED or lock fails.
     */
    public List<String> reserveSeats(Long showId, List<Long> seatIds, String holderId, int holdSeconds) {
        // load seats and validate availability
        List<Seat> seats = seatRepository.findAllById(seatIds);
        for (Seat s : seats) {
            if (s.getStatus() == Seat.SeatStatus.BOOKED) throw new IllegalStateException("Seat already booked: " + s.getId());
            if (!Objects.equals(s.getShow().getId(), showId)) throw new IllegalArgumentException("Seat does not belong to show");
        }

        // attempt to lock all seats in Redis
        List<String> acquired = new ArrayList<>();
        for (Long seatId : seatIds) {
            String key = LOCK_PREFIX + showId + ":" + seatId;
            boolean ok = redisLockService.tryLock(key, holderId, holdSeconds);
            if (!ok) {
                // release already acquired and fail
                acquired.forEach(k -> redisLockService.release(k, holderId));
                throw new IllegalStateException("Could not acquire lock for seat " + seatId);
            }
            acquired.add(key);
        }
        return acquired;
    }

    public void releaseLocks(List<String> keys, String holderId) {
        for (String k : keys) redisLockService.release(k, holderId);
    }
}