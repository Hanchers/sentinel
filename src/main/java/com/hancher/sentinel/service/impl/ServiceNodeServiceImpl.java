package com.hancher.sentinel.service.impl;

import com.hancher.sentinel.dao.mapper.ServiceNodeMapper;
import com.hancher.sentinel.entity.ServiceNode;
import com.hancher.sentinel.service.ServiceNodeService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务节点监控 实现
 * @date 2025-06-19 10:42:38
 * @author hancher
 * @since 1.0
 */
@Service
public class ServiceNodeServiceImpl extends ServiceImpl<ServiceNodeMapper, ServiceNode> implements ServiceNodeService {

    /**
     * 获取集群下某种状态的节点
     *
     * @param clusterId 集群id
     * @return 节点列表
     */
    @Override
    public List<ServiceNode> listClusterNodesByStatus(Long clusterId) {
        return this.list(QueryWrapper.create().eq(ServiceNode::getClusterId, clusterId));
    }


    /**
     * 获取集群下节点数量
     *
     * @param clusterId 集群id
     * @return 数量
     */
    @Override
    public Long countByClusterId(Long clusterId) {
        return this.count(QueryWrapper.create().eq(ServiceNode::getClusterId, clusterId));
    }
}
