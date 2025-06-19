package com.hancher.sentinel.enums;

/**
 * 服务集群状态枚举
 * @date 2025-06-19 09:31:04
 * @author hancher
 * @since 1.0
 */
public enum ServiceClusterStatusEnum {

    /**
     * 下线不可用，or未达到最小存活数
     */
    down,
    /**
     * 上线,达到了最小存活数
     */
    up,
    /**
     * 全部服务存活
     */
    ok,
    /**
     * 本身下线，且等待依赖启动
     */
    wait
    ;
}
