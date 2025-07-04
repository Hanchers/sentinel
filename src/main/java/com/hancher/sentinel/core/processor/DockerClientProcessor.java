package com.hancher.sentinel.core.processor;

import com.github.dockerjava.api.DockerClient;
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

import java.time.Duration;
import java.util.List;

/**
 * DockerProcessor
 * <p/>
 * <a href="https://docs.docker.com/reference/cli/dockerd/#daemon-socket-option">docker daemon 版本文档</a><p/>
 * <a href="https://docs.docker.com/engine/security/protect-access/">docker tls连接</a><p/>
 */
@Component
@Slf4j
public class DockerClientProcessor extends AbstractCmdProcessor {

    /**
     * 支持的命令类型
     *
     * @return 命令类型
     */
    @Override
    public ProcessorTypeEnum supportType() {
        return ProcessorTypeEnum.DOCKER_CLIENT;
    }

    /**
     * 解析命令参数
     *
     * @param param json参数: key 与字段值一致
     * @return 解析结果
     */
    @Override
    public CmdParam parseCmdParam(String param) {
        DockerClientCmdParam dockerParam = super.parseJson(param, DockerClientCmdParam.class);

        if (StringUtils.isBlank(dockerParam.getCertPath())) {
            String certPath = sentinelConfig.getProcessor().getDocker().getCertPath();
            dockerParam.setCertPath(certPath);
        }

        return dockerParam;
    }

    @Override
    public Result process(@Valid CmdParam cmdParam) {
        if (!(cmdParam instanceof DockerClientCmdParam dockerParam)) {
            return Result.fail("参数错误");
        }

        log.info("在服务{}上执行命令：docker {} {}", dockerParam.getTcpHost(),dockerParam.getCmd(), dockerParam.getContainerIdOrName());

        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerParam.getTcpHost())
                .withDockerTlsVerify(true)
                .withDockerCertPath(dockerParam.getCertPath())
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofMinutes(1))
                .responseTimeout(dockerParam.getTimeout())
                .build();


        try (DockerClient dockerClient = DockerClientBuilder.getInstance(config).withDockerHttpClient(httpClient).build()) {
            return processCmd(dockerClient, dockerParam);
        } catch (Exception e) {
            log.error("docker client 命令执行异常: ", e);
            return Result.fail(e.getMessage());
        }
    }



    // todo 优化架构逻辑，更具有扩展性
    private Result  processCmd(DockerClient dockerClient,DockerClientCmdParam dockerParam) {
        switch (dockerParam.getCmd()) {
            case ps -> {
                List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
                for (Container container : containers) {
                    log.debug("Container ID: {}, Image:{}, status:{}, name:{}", container.getId(), container.getImage(), container.getStatus(), container.getNames()[0]);
                }
                return Result.success(containers);
            }
            case ps_filter -> {
                List<Container> containers = dockerClient.listContainersCmd()
                        .withFilter(dockerParam.getArgs().getOrDefault("filterName","id"),List.of(dockerParam.getArgs().getOrDefault("filterValue","id")))
                        .withShowAll(true).exec();
                for (Container container : containers) {
                    log.debug("Container ID: {}, Image:{}, status:{}, name:{}", container.getId(), container.getImage(), container.getStatus(), container.getNames()[0]);
                }
                return Result.success(containers);
            }
            case start -> {
                dockerClient.startContainerCmd(dockerParam.getContainerIdOrName()).exec();
                return Result.success("启动成功");
            }

            case stop -> {
                dockerClient.stopContainerCmd(dockerParam.getContainerIdOrName()).exec();
                return Result.success("停止成功");
            }
        }
        return Result.fail("未知命令");
    }


}
