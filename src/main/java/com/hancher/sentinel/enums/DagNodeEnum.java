package com.hancher.sentinel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * dag 默认节点
 * @date 2025-06-19 10:08:19
 * @author hancher
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum DagNodeEnum {
    /**
     * 默认开始节点
     */
    start(0),
    /**
     * 默认结束节点
     */
    end(-1),
    ;

    private final int code;
}
