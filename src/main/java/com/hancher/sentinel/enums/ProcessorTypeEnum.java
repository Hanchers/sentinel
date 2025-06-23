package com.hancher.sentinel.enums;

import java.util.Optional;

/**
 * 执行器类型
 *
 * @author hancher
 * @date 2025-06-20 14:36:04
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

    // 根据名字获取枚举
    public static Optional<ProcessorTypeEnum> getByName(String name) {
        for (ProcessorTypeEnum value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }
}
