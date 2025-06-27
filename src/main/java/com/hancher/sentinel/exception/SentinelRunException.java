package com.hancher.sentinel.exception;

/**
 * 哨兵统一异常
 *
 * @author hancher
 * @date 2025-06-27 15:19:56
 * @since 1.0
 */
public class SentinelRunException extends RuntimeException {
    public SentinelRunException(String message) {
        super(message);
    }

    public SentinelRunException(String message, Throwable cause) {
        super(message, cause);
    }
}
