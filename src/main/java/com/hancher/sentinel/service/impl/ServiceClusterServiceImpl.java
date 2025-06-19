package com.hancher.sentinel.service.impl;

import com.hancher.sentinel.dao.mapper.ServiceClusterMapper;
import com.hancher.sentinel.entity.ServiceCluster;
import com.hancher.sentinel.service.ServiceClusterService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
/**
 * 集群监控 实现
 * @date 2025-06-19 10:42:38
 * @author hancher
 * @since 1.0
 */
@Service
public class ServiceClusterServiceImpl extends ServiceImpl<ServiceClusterMapper, ServiceCluster> implements ServiceClusterService {
}
