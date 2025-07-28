package com.example.virtual_account.util.genvanumbers;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

public class GenVANumber {
    private final RedissonClient redissonClient;
    private final String prefix;
    private final String redisKey;
    private final int maxSuffix;
    private final int suffixLength;

    public GenVANumber(RedissonClient redissonClient, String prefix, String redisKey, int suffixLength) {
        this.redissonClient = redissonClient;
        this.prefix = prefix;
        this.redisKey = redisKey;
        this.suffixLength = suffixLength;
        this.maxSuffix = (int) Math.pow(10, suffixLength) - 1;
    }

    public String generate() {
        RAtomicLong counter = redissonClient.getAtomicLong(redisKey);
        long next = counter.incrementAndGet();

        if (next > maxSuffix) {
            throw new IllegalStateException("Suffix limit exceeded");
        }

        return prefix + String.format("%0" + suffixLength + "d", next);
    }
}
