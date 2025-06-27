package com.hancher.sentinel.core.health;

import com.hancher.sentinel.core.dto.NodeConfigDTO;
import com.hancher.sentinel.core.dto.Result;
import com.hancher.sentinel.entity.ServiceNode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hancher.sentinel.core.health.AbstractHealthChecker.DEFAULT_SERVICE_NAME;

/**
 * 节点健康检查器分发类
 *
 * @author hancher
 * @date 2025-06-25 17:45:07
 * @since 1.0
 */
@Component
public class HealthCheckerDelegator {
    private final Map<String, HealthChecker> healthCheckerMap = new HashMap<>();

    public HealthCheckerDelegator(List<HealthChecker> list) {
        for (HealthChecker healthChecker : list) {
            healthCheckerMap.put(healthChecker.getCheckStrategy(), healthChecker);
        }
    }


    /**
     * 节点健康检查
     *
     * @param node 节点信息
     * @return 节点检查结果
     */
    public Result checkNode(ServiceNode node) {

        NodeConfigDTO nodeConfig = NodeConfigDTO.of(node.getHealthCheckMethod(), node.getHealthCheckCmd());

        return healthCheckerMap.getOrDefault(nodeConfig.getProcessMethod(), healthCheckerMap.get(DEFAULT_SERVICE_NAME))
                .check(nodeConfig);
    }
}
