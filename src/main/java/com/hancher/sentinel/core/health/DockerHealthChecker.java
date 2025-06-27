package com.hancher.sentinel.core.health;

import com.github.dockerjava.api.model.Container;
import com.hancher.sentinel.core.dto.CmdParam;
import com.hancher.sentinel.core.dto.DockerClientCmdParam;
import com.hancher.sentinel.core.dto.NodeConfigDTO;
import com.hancher.sentinel.core.dto.Result;
import com.hancher.sentinel.core.processor.CmdProcessor;
import com.hancher.sentinel.enums.ProcessorTypeEnum;
import com.hancher.sentinel.enums.SupportHeathCheckerEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * docker 存活检查器
 * @date 2025-06-27 11:40:15
 * @author hancher
 * @since 1.0
 */
@Component
@Slf4j
public class DockerHealthChecker extends AbstractHealthChecker {
    /**
     * 获取检查策略
     *
     * @return 检查策略
     */
    @Override
    public String getCheckStrategy() {
        return SupportHeathCheckerEnum.DOCKER_CHECKER.name();
    }

    /**
     * 检查服务节点是否存活
     *
     * @param node 服务配置
     * @return 结果
     */
    @Override
    public Result check(NodeConfigDTO node) {
        Optional<CmdProcessor> dockerInstance = getProcessorInstance(ProcessorTypeEnum.DOCKER_CLIENT.name());

        if (dockerInstance.isEmpty()) {
            return Result.fail("未找到docker命令处理器");
        }

        CmdProcessor cmdProcessor = dockerInstance.get();
        CmdParam cmdParam = cmdProcessor.parseCmdParam(node.getProcessCmd());

        if (cmdParam instanceof DockerClientCmdParam dockerClientCmdParam) {
            dockerClientCmdParam.setCmd(DockerClientCmdParam.DockerCmd.ps_filter);
            if (StringUtils.isBlank(dockerClientCmdParam.getContainerIdOrName())) {
                return Result.fail("未指定容器ID或名称");
            }
            if (StringUtils.isBlank(dockerClientCmdParam.getTcpHost())) {
                return Result.fail("未指定docker服务host地址");
            }

            dockerClientCmdParam.setArgs(Map.of("filterName", "id", "filterValue", dockerClientCmdParam.getContainerIdOrName()));
            Result process = checkStatus(cmdProcessor.process(dockerClientCmdParam));
            if (process.isSuccess()) {
                return process;
            }
            dockerClientCmdParam.setArgs(Map.of("filterName", "name", "filterValue", dockerClientCmdParam.getContainerIdOrName()));
            process = cmdProcessor.process(dockerClientCmdParam);
            return checkStatus(process);
        }

        return Result.fail("检查失败");

    }

    private Result checkStatus(Result process) {
        if (!process.isSuccess()) {
            return Result.fail("容器健康检查失败");
        }
        if (!(process.getOutput() instanceof List<?> list)){
            return Result.fail("容器健康检查失败");
        }
        List<Container> containers = (List<Container>) list;
        if (containers.isEmpty()) {
            return Result.fail("未找到容器");
        }
        if ("running".equalsIgnoreCase(containers.get(0).getState()) && containers.get(0).getStatus().contains("Up")) {
            return Result.success("容器健康检查成功");
        }

        if ("exited".equalsIgnoreCase(containers.get(0).getState())) {
            return Result.fail("容器已停止");
        }

        return Result.fail("容器健康检查失败");
    }
}
