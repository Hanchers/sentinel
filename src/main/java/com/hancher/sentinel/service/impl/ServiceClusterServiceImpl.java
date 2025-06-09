package com.hancher.sentinel.service.impl;

import com.hancher.sentinel.dao.mapper.ServiceClusterMapper;
import com.hancher.sentinel.entity.ServiceCluster;
import com.hancher.sentinel.service.ServiceClusterService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ServiceClusterServiceImpl extends ServiceImpl<ServiceClusterMapper, ServiceCluster> implements ServiceClusterService {
}
