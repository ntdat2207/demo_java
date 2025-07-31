package com.example.virtual_account.constant;

public enum QueueEnqueueMode {
    ALWAYS, // Luôn đẩy vào, bất kể đã có hay chưa
    IF_SCORE_NOT_EXISTS // Chỉ đẩy nếu chưa có hoặc score khác (dùng cho expired_at)
}
