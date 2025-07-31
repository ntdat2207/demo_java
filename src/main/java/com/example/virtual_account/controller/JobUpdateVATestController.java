package com.example.virtual_account.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.virtual_account.scheduler.VaExpiryScheduler;
import com.example.virtual_account.worker.VaExpiryWorker;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class JobUpdateVATestController {
    private final VaExpiryScheduler scheduler;
    private final VaExpiryWorker worker;

    @PostMapping("/run-job")
    public void runJob() {
        scheduler.scanAndEnqueue();
    }

    @PostMapping("/run-worker")
    public void runWorker() {
        worker.processExpiredVa();
    }
}
