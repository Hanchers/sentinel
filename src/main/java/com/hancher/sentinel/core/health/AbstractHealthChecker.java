package com.hancher.sentinel.core.health;

/**
 * 节点检查器超类
 *
 * @author hancher
 * @date 2025-06-25 10:21:46
 * @since 1.0
 */
public abstract class AbstractHealthChecker implements HealthChecker {

    /**
     * 系统默认处理器
     */
    public static final String DEFAULT_SERVICE_NAME = "defaultHealthChecker";

}
