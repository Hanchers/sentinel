package com.hancher.sentinel.core.config;

import lombok.Data;

import java.util.Map;

/**
 * docker 处理器配置（优化版）
 * <p/>
 * 支持按 host 分别配置证书路径，未匹配到则回退到全局 cert-path
 *
 * @date 2025-06-24 16:44:20
 * @author hancher
 * @since 1.0
 */
@Data
public class SentinelProcessorDockerConfig {
    /**
     * 全局默认证书路径
     */
    private String certPath;

    /**
     * 按主机地址配置独立证书路径
     * 示例：
     *   host-certs[tcp://192.168.1.1:2376] = /path/to/certs-a
     *   host-certs[tcp://192.168.1.2:2376] = /path/to/certs-b
     */
    private Map<String, String> hostCerts;

    /**
     * 空闲连接最大保持时间（分钟），超过后自动回收
     * 默认 30 分钟，设为 -1 表示不过期
     */
    private int maxIdleMinutes = 30;

    /**
     * 根据 host 获取证书路径，无独立配置时回退到全局默认值
     */
    public String getCertPathForHost(String tcpHost) {
        if (hostCerts != null && hostCerts.containsKey(tcpHost)) {
            return hostCerts.get(tcpHost);
        }
        return certPath;
    }
}
