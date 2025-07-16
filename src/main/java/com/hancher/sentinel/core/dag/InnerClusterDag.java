package com.hancher.sentinel.core.dag;

import com.hancher.sentinel.entity.ServiceCluster;
import com.hancher.sentinel.enums.DagNodeEnum;
import com.hancher.sentinel.service.ServiceClusterService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 内部缓存依赖关系构建成图<p/>
 * 支持正向、反向 依赖关系寻找
 *
 * @author hancher
 * @date 2025-06-20 09:05:53
 * @since 1.0
 */
@Slf4j
@Service
public class InnerClusterDag implements InitializingBean {
    /**
     * 集群正向依赖关系
     */
    private Map<Long, Set<Long>> DAG = new HashMap<>();
    /**
     * 集群逆向依赖关系
     */
    private Map<Long, Set<Long>> REVERSE_DAG = new HashMap<>();

    @Resource
    private ServiceClusterService clusterService;

    /**
     * 项目启动时初始化
     */
    @Override
    public void afterPropertiesSet() {
        refresh();
    }

    /**
     * 根据集群依赖关系构建图
     */
    public void refresh() {
        List<ServiceCluster> all = clusterService.list();
        for (ServiceCluster cluster : all) {
            List<String> dependClusters = cluster.getDependClusters();
            // 没有配置上游依赖，默认添加开始节点
            if (CollectionUtils.isEmpty(dependClusters)) {
                dependClusters.add(DagNodeEnum.start.getCode() + "");
            }

            for (String id : dependClusters) {
                Long dependClusterId = Long.valueOf(id);
                DAG.computeIfAbsent(dependClusterId, k -> new HashSet<>()).add(cluster.getId());
                REVERSE_DAG.computeIfAbsent(cluster.getId(), k -> new HashSet<>()).add(dependClusterId);
            }

        }

        // 找到没有下游依赖的终点
        HashSet<Long> ends = new HashSet<>(REVERSE_DAG.keySet());
        ends.removeAll(DAG.keySet());

        for (Long endId : ends) {
            DAG.computeIfAbsent(endId, k -> new HashSet<>()).add(DagNodeEnum.end.getCode());
            REVERSE_DAG.computeIfAbsent(DagNodeEnum.end.getCode(), k -> new HashSet<>()).add(endId);
        }


        // 检查环，不允许环的出现
        checkCircle(new HashSet<>(), DagNodeEnum.start.getCode(),DAG);

    }

    /**
     * 检查环
     * <p/>深度优先遇到重复节点
     *
     * @param exist 从起点开始的所有的节点
     * @param start 开始节点
     */
    private void checkCircle(Set<Long> exist, Long start,Map<Long, Set<Long>> dagTmp) {
        exist.add(start);
        log.debug("环检查过程：{}", exist);

        Set<Long> next = dagTmp.getOrDefault(start, new HashSet<>());
        for (Long id : next) {
            if (exist.contains(id)) {
                log.error("集群存在循环依赖:{}, 环节点：{}", exist, id);
                throw new RuntimeException("集群存在循环依赖");
            }
            checkCircle(exist, id,dagTmp);
        }

        // 移除当前节点， 针对菱形依赖
        exist.remove(start);
    }

    /**
     * 添加节点时检查环
     *
     * @param newNode 新节点
     */
    public void checkCircleWhenAddNode(ServiceCluster newNode) {
        List<String> dependClusters = newNode.getDependClusters();
        // 空不校验
        if (CollectionUtils.isEmpty(dependClusters)) {
            return;
        }
        // 唯一且第一个
        if (dependClusters.size() == 1 && dependClusters.get(0).equals(DagNodeEnum.start.getCode()+"")) {
            return;
        }

        Map<Long, Set<Long>> dagTmp = new HashMap<>(DAG);
        for (String id : newNode.getDependClusters()) {
            dagTmp.computeIfAbsent(Long.parseLong(id), k -> new HashSet<>()).add(Optional.ofNullable(newNode.getId()).orElse(System.currentTimeMillis()));
        }

        checkCircle(new HashSet<>(), DagNodeEnum.start.getCode(),dagTmp);
    }

    /**
     * 依赖的前置节点
     *
     * @param clusterId 节点
     * @return 前置节点组
     */
    public Set<Long> getPre(Long clusterId) {
        return new HashSet<>(REVERSE_DAG.getOrDefault(clusterId, Collections.emptySet()));
    }

    /**
     * 依赖的后置节点
     *
     * @param clusterId 节点
     * @return 后置节点组
     */
    public Set<Long> getNext(Long clusterId) {
        return new HashSet<>(DAG.getOrDefault(clusterId, Collections.emptySet()));
    }


    /**
     * 从集群组中获取最前置的节点组
     *
     * @param clusterIds 集群id 组
     * @return
     */
    public Set<Long> getFirst(List<Long> clusterIds) {
        Set<Long> first = new HashSet<>(clusterIds);
        Set<Long> tmp = new HashSet<>(first);

        for (Long clusterId : clusterIds) {
            Set<Long> pre = getPre(clusterId);
            // 如果前置节点 扔给在给的集合中，则去掉当前节点
            pre.retainAll(tmp);
            // 没有交集，说明没有前置节点
            if (!pre.isEmpty()) {
                first.remove(clusterId);
            }
        }

        return first;
    }
}
