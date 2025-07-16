package com.hancher.sentinel.service;

import com.hancher.sentinel.entity.DependentDag;
import com.mybatisflex.core.service.IService;

/**
 * 项目依赖dag服务
 *
 * @author hancher
 * @date 2025-06-19 10:41:15
 * @since 1.0
 */
@Deprecated
public interface DependentDagService extends IService<DependentDag> {

    /**
     * 删除依赖关系
     *
     * @param sourceClusterId 源集群id
     * @param targetClusterId 目标集群id
     * @return 添加结果
     */
    boolean remove(Long sourceClusterId, Long targetClusterId);
}