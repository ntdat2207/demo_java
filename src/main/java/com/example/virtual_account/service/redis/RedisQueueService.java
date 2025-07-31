package com.example.virtual_account.service.redis;

import java.time.Instant;
import java.util.Collection;

import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.example.virtual_account.constant.QueueEnqueueMode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RedisQueueService {
    RedissonClient redissonClient;

    public void enqueue(String redisKey, String member, Instant scoreAt, QueueEnqueueMode mode) {
        RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(redisKey);
        long newScore = scoreAt.toEpochMilli();

        if (mode == QueueEnqueueMode.ALWAYS) {
            queue.add(newScore, member);
            return;
        }

        Double existingScore = queue.getScore(member);
        if (existingScore == null || existingScore.longValue() != newScore) {
            queue.add(newScore, member);
        }
    }

    public Collection<String> dequeueDue(String redisKey, Instant now) {
        RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(redisKey);
        Collection<String> due = queue.valueRange(0, true, now.toEpochMilli(), true);
        for (String member : due) {
            queue.remove(member);
        }
        return due;
    }

    public void remove(String redisKey, String member) {
        RScoredSortedSet<String> queue = redissonClient.getScoredSortedSet(redisKey);
        queue.remove(member);
    }
}
