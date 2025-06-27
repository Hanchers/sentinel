package com.hancher.sentinel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对外暴露，支持的健康检查器
 * @date 2025-06-27 11:44:56
 * @author hancher
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum SupportHeathCheckerEnum {
    /**
     * 走默认检查器
     */
    BASH("linux 命令行"),
    DOCKER_CLIENT("docker 命令行"),
    DOCKER_CHECKER("默认docker检查器"),
    ;

    private final String showName;
}
