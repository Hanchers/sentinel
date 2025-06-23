package com.hancher.sentinel.service;

import com.hancher.sentinel.entity.ServiceCluster;
import com.hancher.sentinel.enums.ServiceClusterStatusEnum;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 集群服务
 * @date 2025-06-19 10:41:37
 * @author hancher
 * @since 1.0
 */
public interface ServiceClusterService extends IService<ServiceCluster> {

    /**
     * 根据状态查询
     * @param status 状态
     * @return 集群列表
     */
    List<ServiceCluster> listByStatus(ServiceClusterStatusEnum... status);

    /**
     * 根据id查询
     * @param ids id列表
     * @return 集群列表
     */
    List<ServiceCluster> listByIds(List<Long> ids);

}