package com.hancher.sentinel.enums;
/**
 * 执行器类型
 * @date 2025-06-20 14:36:04
 * @author hancher
 * @since 1.0
 */
public enum ProcessorTypeEnum {
    /**
     * linux 命令行
     */
    BASH,
    /**
     * docker 客户端
     */
    DOCKER_CLIENT,
    ;
}
