package com.hancher.sentinel.core.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 项目配置文件
 * @date 2025-06-24 16:30:01
 * @author hancher
 * @since 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "sentinel")
@Validated
public class SentinelConfig {

    /**
     * 系统版本
     */
    @NotBlank(message = "系统版本不能为空")
    private String version;

    /**
     * 处理器配置
     */
    private SentinelProcessorConfig processor;

}
