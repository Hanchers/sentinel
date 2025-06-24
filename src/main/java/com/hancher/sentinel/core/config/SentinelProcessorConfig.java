package com.hancher.sentinel.core.config;

import lombok.Data;
/**
 * 处理器配置
 * @date 2025-06-24 16:44:37
 * @author hancher
 * @since 1.0
 */
@Data
public class SentinelProcessorConfig {
    private SentinelProcessorDockerConfig docker;
}
