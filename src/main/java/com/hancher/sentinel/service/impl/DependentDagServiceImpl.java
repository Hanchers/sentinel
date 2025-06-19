package com.hancher.sentinel.service.impl;

import com.hancher.sentinel.dao.mapper.DependentDagMapper;
import com.hancher.sentinel.entity.DependentDag;
import com.hancher.sentinel.service.DependentDagService;
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
}
