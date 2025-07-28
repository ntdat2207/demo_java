package com.example.virtual_account.service;

import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.example.virtual_account.util.genvanumbers.GenVANumber;

@Service
public class GenVANumberService {
    private final RedissonClient redissonClient;

    public GenVANumberService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public String generate(String prefix, String redisKey, int suffixLength) {
        return new GenVANumber(redissonClient, prefix, redisKey, suffixLength).generate();
    }
}
