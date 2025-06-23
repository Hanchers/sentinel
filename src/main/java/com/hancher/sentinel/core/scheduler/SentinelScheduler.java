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
        List<ServiceCluster> downClusters = clusterService.listByStatus(ServiceClusterStatusEnum.down,ServiceClusterStatusEnum.up);
        if (downClusters.isEmpty()) {
            log.debug("无下线或等待集群，跳过。。");
            return;
        }
        Map<Long, ServiceCluster> map = downClusters.stream().collect(Collectors.toMap(ServiceCluster::getId, Function.identity(), (o1, o2) -> o1));
        // 根据依赖关系，找到最前置节点
        Set<Long> first = innerClusterDag.getFirst(new ArrayList<>(map.keySet()));

        first.forEach(id -> restartCluster(map.get(id)));
        // 前置节点启动成功，尝试通知下游 wait cluster
        // 当然，这部分通知逻辑也可以省略，多跑几次循环也能实现类似效果。如果以后逻辑变复杂，考虑舍弃这部分逻辑
        first.forEach(id -> noticeWaitCluster(map.get(id)));
    }


    /**
     * 扫描重启下线节点
     */
    private void restartCluster(ServiceCluster cluster) {
        if (Objects.isNull(cluster)) {
            return;
        }
        log.info("\n===========================================================");
        log.info("--尝试恢复下线集群：{}({}), 状态：{}", cluster.getName(), cluster.getId(), cluster.getStatus());
        // 再次验证状态, 这里认为集群状态是准确的，集群状态准确度由项目健康扫码模块保证
        if (cluster.getStatus() == ServiceClusterStatusEnum.ok) {
            return;
        }
        // 不健康集群的节点
        int success = 0;
        List<ServiceNode> nodes = nodeService.listClusterNodesByStatus(cluster.getId());
        for (ServiceNode node : nodes) {
            success += restartNode(node);
        }

        ServiceClusterStatusEnum status = ServiceClusterStatusEnum.down;
        if (success == nodes.size()) {
            log.info("--集群{{}({})节点全部恢复",  cluster.getName(), cluster.getId());
            status = ServiceClusterStatusEnum.ok;
        } else if (success >= cluster.getMinAliveNum()){
            log.info("--集群{}({})节点达到最小存活数量",  cluster.getName(), cluster.getId());
            status = ServiceClusterStatusEnum.up;
        } else  {
            log.info("--集群{}({})恢复失败",  cluster.getName(), cluster.getId());
        }

        if (cluster.getStatus() != status) {
            cluster.setStatus(status);
            cluster.setUpdateTime(LocalDateTime.now());
            clusterService.updateById(cluster);
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
     * 通知等待集群
     * @param preCluster 前置集群
     */
    private void noticeWaitCluster(ServiceCluster preCluster) {
        // 验证前置集群状态
        if (preCluster.getStatus() == ServiceClusterStatusEnum.down
                || preCluster.getStatus() == ServiceClusterStatusEnum.wait) {
            log.info("激活下游集群任务，前置集群{}({})状态为{}未恢复，跳过", preCluster.getName(),preCluster.getId(), preCluster.getStatus());
            return;
        }

        Set<Long> next = innerClusterDag.getNext(preCluster.getId());
        List<ServiceCluster> waitClusters = clusterService.listByIds(next);

        for (ServiceCluster waitCluster : waitClusters) {
            if (waitCluster.getStatus() == ServiceClusterStatusEnum.ok) {
                log.info("当前集群{}({})已完全恢复，跳过", waitCluster.getName(),waitCluster.getId());
                continue;
            }
            // 重启 wait, down, up 状态的集群
            restartCluster(waitCluster);
        }
    }

    /**
     * 项目依赖状态扫描
     */
    private void scanProjectHealth() {
        log.info("重新扫描项目依赖情况，更新集群状态图");

        //
    }

}
