package com.hancher.sentinel.enums;

import com.hancher.sentinel.web.vo.SentinelKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 对外暴露，支持的重启方法
 *
 * @author hancher
 * @date 2025-06-27 11:44:56
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum SupportRestarterEnum {
    /**
     * 走默认检查器
     */
    BASH("linux 命令行"),
    DOCKER_CLIENT("docker 命令行"),
    ;

    private final String showName;

    /**
     * 重启方法 下拉选项
     *
     * @return SentinelKey对象列表，每个对象包含枚举值和显示名称
     */
    public static List<SentinelKey> listOption() {
        // 将SupportRestartEnum枚举转换为SentinelKey对象列表
        return Arrays.stream(SupportRestarterEnum.values())
                .map(e -> SentinelKey.builder().value(e.name()).text(e.getShowName())
                        .build())
                .toList();
    }

}
