package com.hancher.sentinel.core.processor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.hancher.sentinel.core.dto.CmdParam;
import com.hancher.sentinel.core.dto.DockerClientCmdParam;
import com.hancher.sentinel.core.dto.Result;
import com.hancher.sentinel.enums.ProcessorTypeEnum;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DockerProcessor - 优化版
 * <p/>
 * 核心改进：
 * 1. 按 Docker 主机地址缓存 DockerClient，复用 TLS 连接，避免每次调用重新建连
 * 2. 连接前执行 ping 健康检查，快速发现不可用连接
 * 3. 精细化错误处理，区分网络超时/TLS证书/Docker daemon 错误
 * 4. 合理的超时配置，避免任务堆积
 * <p/>
 * <a href="https://docs.docker.com/reference/cli/dockerd/#daemon-socket-option">docker daemon 版本文档</a><p/>
 * <a href="https://docs.docker.com/engine/security/protect-access/">docker tls连接</a><p/>
 */
@Component
@Slf4j
public class DockerClientProcessor extends AbstractCmdProcessor {

    /**
     * 连接超时时间（探活场景建连应该在秒级完成）
     */
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(10);

    /** 响应超时兜底 */
    private static final Duration DEFAULT_RESPONSE_TIMEOUT = Duration.ofSeconds(30);

    /** 连接池最大连接数（单 host 并发量很小，连接数降到合理值） */
    private static final int MAX_CONNECTIONS_PER_HOST = 5;

    /**
     * DockerClient 缓存：key = tcpHost + "|" + certPath，value = DockerClient 包装
     * 使用 ConcurrentHashMap 保证线程安全
     */
    private final Map<String, CachedDockerClient> clientCache = new ConcurrentHashMap<>();

    /**
     * 支持的命令类型
     */
    @Override
    public ProcessorTypeEnum supportType() {
        return ProcessorTypeEnum.DOCKER_CLIENT;
    }

    /**
     * 解析命令参数
     */
    @Override
    public CmdParam parseCmdParam(String param) {
        DockerClientCmdParam dockerParam = super.parseJson(param, DockerClientCmdParam.class);

        if (StringUtils.isBlank(dockerParam.getCertPath())) {
            // 按主机地址匹配独立证书配置，未匹配则回退到全局默认证书
            String certPath = sentinelConfig.getProcessor().getDocker().getCertPathForHost(dockerParam.getTcpHost());
            dockerParam.setCertPath(certPath);
        }


        // 如果未指定超时或超时过长，使用合理默认值
        if (dockerParam.getTimeout() == null || dockerParam.getTimeout().compareTo(DEFAULT_RESPONSE_TIMEOUT) > 0) {
            dockerParam.setTimeout(DEFAULT_RESPONSE_TIMEOUT);
        }

        return dockerParam;
    }

    @Override
    public Result process(@Valid CmdParam cmdParam) {
        if (!(cmdParam instanceof DockerClientCmdParam dockerParam)) {
            return Result.fail("参数错误");
        }

        log.debug("在服务{}上执行命令：docker {} {}", dockerParam.getTcpHost(), dockerParam.getCmd(), dockerParam.getContainerIdOrName());

        DockerClient dockerClient = null;
        try {
            dockerClient = getOrCreateDockerClient(dockerParam);
            return processCmd(dockerClient, dockerParam);
        } catch (ConnectException e) {
            log.error("Docker 主机 {} 连接失败，请检查网络或 Docker daemon 是否启动", dockerParam.getTcpHost(), e);
            evictClient(dockerParam);
            return Result.fail("Docker 主机连接失败: " + dockerParam.getTcpHost());
        } catch (SocketTimeoutException e) {
            log.error("Docker 主机 {} 连接超时", dockerParam.getTcpHost(), e);
            evictClient(dockerParam);
            return Result.fail("Docker 主机连接超时: " + dockerParam.getTcpHost());
        } catch (DockerException e) {
            log.error("Docker 命令执行异常: host={}, cmd={}", dockerParam.getTcpHost(), dockerParam.getCmd(), e);
            return Result.fail("Docker 命令执行失败: " + (e.getMessage() != null ? e.getMessage() : "未知错误"));
        } catch (Exception e) {
            log.error("Docker client 未知异常", e);
            return Result.fail("Docker 操作异常: " + e.getMessage());
        }
    }

    /**
     * 获取或创建 DockerClient（带缓存）
     */
    private DockerClient getOrCreateDockerClient(DockerClientCmdParam dockerParam) throws Exception {
        String cacheKey = dockerParam.getTcpHost() + "|" + dockerParam.getCertPath();
        CachedDockerClient cached = clientCache.get(cacheKey);

        // 检查缓存是否有效
        if (cached != null && isClientHealthy(cached.dockerClient)) {
            return cached.dockerClient;
        }

        // 新建连接
        synchronized (this) {
            // 双重检查
            cached = clientCache.get(cacheKey);
            if (cached != null && isClientHealthy(cached.dockerClient)) {
                return cached.dockerClient;
            }

            log.info("创建新的 Docker 连接: host={}", dockerParam.getTcpHost());

            DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost(dockerParam.getTcpHost())
                    .withDockerTlsVerify(true)
                    .withDockerCertPath(dockerParam.getCertPath())
                    .build();


            DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(config.getDockerHost())
                    .sslConfig(config.getSSLConfig())
                    .maxConnections(MAX_CONNECTIONS_PER_HOST)
                    .connectionTimeout(CONNECTION_TIMEOUT)
                    .responseTimeout(dockerParam.getTimeout())
                    .build();

            DockerClient dockerClient = DockerClientBuilder.getInstance(config)
                    .withDockerHttpClient(httpClient)
                    .build();

            // 连接健康检查
            dockerClient.pingCmd().exec();
            log.info("Docker 连接建立成功: host={}, dockerVersion={}",
                    dockerParam.getTcpHost(),
                    dockerClient.versionCmd().exec().getVersion());

            cached = new CachedDockerClient(dockerClient, System.currentTimeMillis());
            clientCache.put(cacheKey, cached);
            return dockerClient;
        }
    }

    /**
     * 检查 DockerClient 是否健康
     */
    private boolean isClientHealthy(DockerClient client) {
        try {
            client.pingCmd().exec();
            return true;
        } catch (Exception e) {
            log.warn("Docker 连接健康检查失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从缓存中移除失效的客户端
     */
    private void evictClient(DockerClientCmdParam dockerParam) {
        String cacheKey = dockerParam.getTcpHost() + "|" + dockerParam.getCertPath();
        CachedDockerClient removed = clientCache.remove(cacheKey);
        if (removed != null) {
            try {
                removed.dockerClient.close();
            } catch (Exception e) {
                log.debug("关闭失效 DockerClient 时忽略: {}", e.getMessage());
            }
        }
    }

    /**
     * 清理所有缓存连接（可用于定时任务清理长时间未使用的连接）
     */
    public void evictIdleClients(long idleThresholdMs) {
        long now = System.currentTimeMillis();
        clientCache.entrySet().removeIf(entry -> {
            if (now - entry.getValue().createTime > idleThresholdMs) {
                log.info("回收空闲 Docker 连接: hostKey={}", entry.getKey());
                try {
                    entry.getValue().dockerClient.close();
                } catch (Exception e) {
                    log.debug("关闭空闲 DockerClient 时忽略: {}", e.getMessage());
                }
                return true;
            }
            return false;
        });
    }

    private Result processCmd(DockerClient dockerClient, DockerClientCmdParam dockerParam) {
        switch (dockerParam.getCmd()) {
            case ps -> {
                List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
                log.debug("列出所有容器, 总数: {}", containers.size());
                return Result.success(containers);
            }
            case ps_filter -> {
                List<Container> containers = dockerClient.listContainersCmd()
                        .withFilter(
                                dockerParam.getArgs().getOrDefault("filterName", "id"),
                                List.of(dockerParam.getArgs().getOrDefault("filterValue", ""))
                        )
                        .withShowAll(true).exec();
                log.debug("按条件查询容器, 匹配数: {}", containers.size());
                return Result.success(containers);
            }
            case start -> {
                dockerClient.startContainerCmd(dockerParam.getContainerIdOrName()).exec();
                log.info("容器启动成功: {}", dockerParam.getContainerIdOrName());
                return Result.success("启动成功");
            }
            case stop -> {
                dockerClient.stopContainerCmd(dockerParam.getContainerIdOrName()).exec();
                log.info("容器停止成功: {}", dockerParam.getContainerIdOrName());
                return Result.success("停止成功");
            }
        }
        return Result.fail("未知命令: " + dockerParam.getCmd());
    }

    /**
     * 缓存的 DockerClient 包装
     */
    private static class CachedDockerClient {
        final DockerClient dockerClient;
        final long createTime;

        CachedDockerClient(DockerClient dockerClient, long createTime) {
            this.dockerClient = dockerClient;
            this.createTime = createTime;
        }
    }
}
