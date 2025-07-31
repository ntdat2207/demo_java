package com.example.virtual_account.service.redis;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisLockService {
    RedissonClient redissonClient;

    /**
     * Executes the given action within a Redis lock block.
     *
     * @param key        The lock key (must be unique for each resource)
     * @param ttlSeconds The time-to-live (TTL) for the lock in seconds
     * @param action     The function to execute while the lock is held
     * @return The result returned by the action
     * @throws Exception If the action fails or the lock cannot be acquired
     */

    public <T> T executeWithLock(String key, long ttlSeconds, Callable<T> action) throws Exception {
        RLock lock = redissonClient.getLock(key);
        boolean locked = false;
        try {
            locked = lock.tryLock(0, ttlSeconds, TimeUnit.SECONDS);
            if (!locked) {
                throw new IllegalStateException("Resource is already being processed. Key: " + key);
            }

            return action.call();

        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void executeWithLock(String key, long ttlSeconds, Runnable action) {
        RLock lock = redissonClient.getLock(key);
        boolean locked = false;
        try {
            locked = lock.tryLock(0, ttlSeconds, TimeUnit.SECONDS);
            if (!locked) {
                throw new IllegalStateException("Resource is already being processed. Key: " + key);
            }
            action.run();
        } catch (Exception e) {
            log.error("Error executing locked action", e);
            throw new RuntimeException(e); // or rethrow e depending on needs
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
