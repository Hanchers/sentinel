package com.hancher.sentinel.service.impl;

import com.hancher.sentinel.dao.mapper.ServiceClusterMapper;
import com.hancher.sentinel.entity.ServiceCluster;
import com.hancher.sentinel.enums.DagNodeEnum;
import com.hancher.sentinel.enums.ServiceClusterStatusEnum;
import com.hancher.sentinel.service.ServiceClusterService;
import com.hancher.sentinel.web.vo.SentinelKey;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<ServiceCluster> listByStatus(ServiceClusterStatusEnum... status) {
        return this.list(QueryWrapper.create().in(ServiceCluster::getStatus, Arrays.stream(status).toList()));
    }

    /**
     * 根据id查询
     *
     * @param ids id列表
     * @return 集群列表
     */
    @Override
    public List<ServiceCluster> listByIds(List<Long> ids) {
        return this.list(QueryWrapper.create().in(ServiceCluster::getId, ids));
    }

    /**
     * 获取集群选项
     *
     * @return 集群选项
     */
    @Override
    public List<SentinelKey> listClusterOption(boolean includeStart) {
        List<ServiceCluster> list = this.list();

        List<SentinelKey> clusterOption = new ArrayList<>();
        if (includeStart) {
            clusterOption.add(SentinelKey.builder().value(DagNodeEnum.start.getCode() + "").text("开始节点").build());
        }
        for (ServiceCluster serviceCluster : list) {
            clusterOption.add(SentinelKey.builder().value(serviceCluster.getId() + "").text(serviceCluster.getName()).build());
        }

        return clusterOption;
    }
}
