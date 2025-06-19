package com.hancher.sentinel.service.impl;

import com.hancher.sentinel.dao.mapper.ServiceNodeMapper;
import com.hancher.sentinel.entity.ServiceNode;
import com.hancher.sentinel.service.ServiceNodeService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
/**
 * 服务节点监控 实现
 * @date 2025-06-19 10:42:38
 * @author hancher
 * @since 1.0
 */
@Service
public class ServiceNodeServiceImpl extends ServiceImpl<ServiceNodeMapper, ServiceNode> implements ServiceNodeService {
}
