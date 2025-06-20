package com.hancher.sentinel.core.scheduler;

import com.hancher.sentinel.core.dag.InnerClusterDag;
import com.hancher.sentinel.entity.ServiceCluster;
import com.hancher.sentinel.entity.ServiceNode;
import com.hancher.sentinel.enums.ServiceClusterStatusEnum;
import com.hancher.sentinel.enums.ServiceNodeStatusEnum;
import com.hancher.sentinel.service.ServiceClusterService;
import com.hancher.sentinel.service.ServiceNodeService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 定时触发器
 */
@Slf4j
@Service
@AllArgsConstructor
public class SentinelScheduler {
    private final ServiceNodeService nodeService;
    private final InnerClusterDag innerClusterDag;
    private final ServiceClusterService clusterService;

    /**
     * 核心心跳探活任务
     * <p/>
     * 判断有没有下线的节点 <p/>
     * 修复重启下线节点/集群。如果没有跳过<p/>
     * 再进行一遍完整的项目依赖状态扫描<p/>
     */
    @SneakyThrows
    @Scheduled(initialDelay = 5000, fixedDelay = 60000)
    public void scanTask() {
        log.info("开始执行定时任务：{}", LocalDateTime.now());
        // 扫描重启下线节点、集群
        scanFailCluster();
        // 项目依赖状态扫描
        scanProjectHealth();
    }

    /**
     * 集群状态扫描
     */
    private void scanFailCluster() {
        log.info("扫描下线集群，尝试恢复下线集群");
        // 查询所有下线的集群
        List<ServiceCluster> downClusters = clusterService.selectListByStatus(ServiceClusterStatusEnum.down,ServiceClusterStatusEnum.up);
        if (downClusters.isEmpty()) {
            log.debug("无下线或等待集群，跳过。。");
            return;
        }
        Map<Long, ServiceCluster> map = downClusters.stream().collect(Collectors.toMap(ServiceCluster::getId, Function.identity(), (o1, o2) -> o1));
        // 根据依赖关系，找到最前置节点
        Set<Long> first = innerClusterDag.getFirst(new ArrayList<>(map.keySet()));

        first.forEach(id -> restartCluster(map.get(id)));
    }


    /**
     * 扫描重启下线节点
     */
    private void restartCluster(ServiceCluster cluster) {
        if (Objects.isNull(cluster)) {
            return;
        }
        log.info("\n===========================================================");
        log.info("--尝试恢复下线集群：{},{}", cluster.getId(), cluster.getName());
        // 再次验证状态, 这里认为集群状态是准确的，集群状态准确度由项目健康扫码模块保证
        if (cluster.getStatus() == ServiceClusterStatusEnum.wait
                || cluster.getStatus() == ServiceClusterStatusEnum.ok) {
            return;
        }
        // 不健康集群的节点
        List<ServiceNode> downNodes = nodeService.selectClusterNodesByStatus(cluster.getId());
        for (ServiceNode node : downNodes) {
            int success = restartNode(node);
        }

    }

    /**
     * 扫描重启下线节点
     * @return 1成功，0失败
     */
    private int restartNode(ServiceNode node) {
        log.info("----扫描服务节点状态：{}，{}的状态为{}", node.getId(), node.getName(), node.getStatus());

        if (node.getStatus() == ServiceNodeStatusEnum.ok) {
            return 1;
        }

        // 下线节点尝试重启
        String restartMethod = node.getRestartMethod();
        System.out.println("尝试重启节点：" + restartMethod);
        System.out.println("重启命令：" + node.getRestartCmd());

        return 0;
    }


    /**
     * 项目依赖状态扫描
     */
    private void scanProjectHealth() {
        log.info("重新扫描项目依赖情况，更新集群状态图");

        //
    }

}
