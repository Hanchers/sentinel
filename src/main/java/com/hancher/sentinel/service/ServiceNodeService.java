package com.hancher.sentinel.service;

import com.hancher.sentinel.entity.ServiceNode;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 节点服务
 * @date 2025-06-19 10:40:54
 * @author hancher
 * @since 1.0
 */
public interface ServiceNodeService extends IService<ServiceNode> {

    /**
     * 获取集群下某种状态的节点
     * @param clusterId 集群id
     * @return 节点列表
     */
    List<ServiceNode> selectClusterNodesByStatus(Long clusterId);

    /**
     * 获取集群下节点数量
     * @param clusterId 集群id
     * @return 数量
     */
    Long countByClusterId(Long clusterId);

}