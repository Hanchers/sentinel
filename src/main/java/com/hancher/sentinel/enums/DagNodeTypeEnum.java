package com.hancher.sentinel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * dag 节点类型
 * @date 2025-10-10 15:57:00
 * @author hancher
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum DagNodeTypeEnum {
    /**
     * 集群
     */
    cluster,
    /**
     * 服务节点
     */
    node,
    ;
}
