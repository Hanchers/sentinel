package com.hancher.sentinel.enums;

import com.hancher.sentinel.web.vo.SentinelKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

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
    BASH("linux命令行"),
    DOCKER_CLIENT("docker客户端"),
    DOCKER_CHECKER("docker检查器"),
    ;

    private final String showName;

    /**
     * 健康检查器 下拉选择列表
     *
     * @return SentinelKey对象列表，每个对象包含枚举值和显示名称
     */
    public static List<SentinelKey> listOption() {
        // 将SupportRestartEnum枚举转换为SentinelKey对象列表
        return Arrays.stream(SupportHeathCheckerEnum.values())
                .map(e -> SentinelKey.builder().value(e.name()).text(e.getShowName())
                        .build())
                .toList();
    }
}
