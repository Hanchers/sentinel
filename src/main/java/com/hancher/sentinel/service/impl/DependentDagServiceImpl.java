package com.hancher.sentinel.service.impl;

import com.hancher.sentinel.dao.mapper.DependentDagMapper;
import com.hancher.sentinel.entity.DependentDag;
import com.hancher.sentinel.service.DependentDagService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
/**
 * dag 实现
 * @date 2025-06-19 10:42:38
 * @author hancher
 * @since 1.0
 */
@Service
public class DependentDagServiceImpl extends ServiceImpl<DependentDagMapper, DependentDag> implements DependentDagService {

    /**
     * 删除依赖关系
     *
     * @param sourceClusterId 源集群id
     * @param targetClusterId 目标集群id
     * @return 添加结果
     */
    @Override
    public boolean remove(Long sourceClusterId, Long targetClusterId) {
        return remove(QueryWrapper.create()
                .eq(DependentDag::getSourceClusterId, sourceClusterId)
                .eq(DependentDag::getTargetClusterId, targetClusterId));
    }
}
