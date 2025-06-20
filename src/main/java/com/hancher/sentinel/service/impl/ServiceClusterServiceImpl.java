package com.hancher.sentinel.service.impl;

import com.hancher.sentinel.dao.mapper.ServiceClusterMapper;
import com.hancher.sentinel.entity.ServiceCluster;
import com.hancher.sentinel.enums.ServiceClusterStatusEnum;
import com.hancher.sentinel.service.ServiceClusterService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 集群监控 实现
 * @date 2025-06-19 10:42:38
 * @author hancher
 * @since 1.0
 */
@Service
public class ServiceClusterServiceImpl extends ServiceImpl<ServiceClusterMapper, ServiceCluster> implements ServiceClusterService {

    /**
     * 根据状态查询
     *
     * @param status 状态
     * @return 集群列表
     */
    @Override
    public List<ServiceCluster> selectListByStatus(ServiceClusterStatusEnum... status) {
        return this.list(QueryWrapper.create().in(ServiceCluster::getStatus, Arrays.stream(status).toList()));
    }
}
