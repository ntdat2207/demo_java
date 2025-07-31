package com.example.virtual_account.worker;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
public class VaExpiryWorker {
    private final RedisQueueService queueService;
    private final VirtualAccountRepository repository;
    private final RedisLockService redisLockService;

    private static final String LOCK_KEY = "lock:va_expiry_worker";
    private static final int MAX_RETRY = 3;

    @Scheduled(fixedRate = 5000)
    public void processExpiredVa() {

        try {
            redisLockService.executeWithLock(LOCK_KEY, 30, () -> {
                Instant now = Instant.now();
                Collection<String> dueIds = queueService.dequeueDue(VirtualAccountConstant.QUEUE_UPDATE_VA_STATUS, now);
                log.info("Processing {} expired VAs", dueIds.size());

                for (String id : dueIds) {
                    try {
                        Optional<VirtualAccountEntity> optional = repository.findById(Long.valueOf(id));
                        if (optional.isPresent()) {
                            VirtualAccountEntity va = optional.get();
                            Instant expiredAt = va.getExpiredAt().atZone(ZoneId.systemDefault()).toInstant();

                            if (va.getStatus() == VirtualAccountConstant.STATUS_ACTIVE
                                    && expiredAt.isBefore(now)) {
                                va.setStatus(VirtualAccountConstant.STATUS_INACTIVE);
                                repository.save(va);
                                log.info("VA {} expired at {}, marked as INACTIVE", id, expiredAt);

                                // Xoá khỏi queue và retry count
                                queueService.remove(VirtualAccountConstant.QUEUE_UPDATE_VA_STATUS, id);
                                queueService.clearRetryCount(VirtualAccountConstant.QUEUE_UPDATE_VA_STATUS, id);
                            }
                        } else {
                            // Không tìm thấy => xoá khỏi queue
                            log.warn("VA {} not found in DB", id);
                            queueService.remove(VirtualAccountConstant.QUEUE_UPDATE_VA_STATUS, id);
                            queueService.clearRetryCount(VirtualAccountConstant.QUEUE_UPDATE_VA_STATUS, id);
                        }
                    } catch (Exception ex) {
                        // Retry: tăng count, nếu vượt quá MAX_RETRY thì xoá
                        int retryCount = queueService.incrementRetryCount(VirtualAccountConstant.QUEUE_UPDATE_VA_STATUS,
                                id);
                        if (retryCount >= MAX_RETRY) {
                            log.error("VA {} failed {} times, removing from queue", id, retryCount);
                            queueService.remove(VirtualAccountConstant.QUEUE_UPDATE_VA_STATUS, id);
                            queueService.clearRetryCount(VirtualAccountConstant.QUEUE_UPDATE_VA_STATUS, id);
                            // Optional: push vào dead-letter queue ở đây nếu cần
                        } else {
                            log.warn("VA {} failed {} times, will retry", id, retryCount, ex);
                        }
                    }
                }
            });
        } catch (Exception e) {
            log.error("Error processing VA expiry", e);
        } finally {

        }
    }
}
