package com.example.virtual_account.scheduler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.virtual_account.constant.QueueEnqueueMode;
import com.example.virtual_account.constant.VirtualAccountConstant;
import com.example.virtual_account.entity.VirtualAccountEntity;
import com.example.virtual_account.repository.VirtualAccountRepository;
import com.example.virtual_account.service.redis.RedisLockService;
import com.example.virtual_account.service.redis.RedisQueueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class VaExpiryScheduler {
    private final VirtualAccountRepository repository;
    private final RedisQueueService queueService;
    private final RedisLockService redisLockService;

    private static final String LOCK_KEY = "lock:va_expiry_scheduler";

    // Job quét mỗi 1 phút
    @Scheduled(fixedRate = 60000)
    public void scanAndEnqueue() {
        try {
            redisLockService.executeWithLock(LOCK_KEY, 30, () -> {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime nextWindow = now.plus(Duration.ofMinutes(5));

                List<VirtualAccountEntity> toExpireSoon = repository
                        .findByStatusAndExpiredAt(VirtualAccountConstant.STATUS_ACTIVE, now, nextWindow);

                for (VirtualAccountEntity va : toExpireSoon) {
                    queueService.enqueue(VirtualAccountConstant.QUEUE_UPDATE_VA_STATUS, String.valueOf(va.getId()),
                            va.getExpiredAt().atZone(ZoneId.systemDefault()).toInstant(),
                            QueueEnqueueMode.IF_SCORE_NOT_EXISTS);
                }

                log.info("Scheduled {} VAs to expire", toExpireSoon.size());
            });
        } catch (Exception e) {
            log.error("Error scheduling VA expiry", e);
        }
    }
}
