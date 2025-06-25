package com.hancher.sentinel.core.starter;

import com.hancher.sentinel.core.dto.NodeConfigDTO;
import com.hancher.sentinel.core.dto.Result;
import com.hancher.sentinel.entity.ServiceNode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.hancher.sentinel.core.starter.AbstractNodeStarter.DEFAULT_SERVICE_NAME;

/**
 * 节点启动器分发类
 *
 * @author hancher
 * @date 2025-06-25 17:44:14
 * @since 1.0
 */
@Component
public class NodeStarterDelegator {


    @Resource
    private Map<String, NodeStarter> nodeStarterMap;

    /**
     * 启动服务节点
     *
     * @param node 节点参数
     * @return 启动结果
     */
    public Result restartNode(ServiceNode node) {
        NodeConfigDTO nodeConfig = NodeConfigDTO.of(node.getHealthCheckMethod(), node.getHealthCheckCmd());

        return nodeStarterMap.getOrDefault(nodeConfig.getProcessMethod(), nodeStarterMap.get(DEFAULT_SERVICE_NAME))
                .restart(nodeConfig);
    }
}
