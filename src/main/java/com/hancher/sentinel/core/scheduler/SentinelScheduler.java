package com.hancher.sentinel.core.scheduler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 定时触发器
 */
@Slf4j
@Service
public class SentinelScheduler {

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
        scanCluster();
        // 项目依赖状态扫描
        scanProjectDependency();
    }

    /**
     * 集群状态扫描
     */
    private void scanCluster() {

    }

    /**
     * 扫描重启下线节点
     */
    private void scanRestartNode() {

    }


    /**
     * 项目依赖状态扫描
     */
    private void scanProjectDependency() {

    }

}
