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
import java.util.Optional;

/**
 * docker 存活检查器（优化版）
 * <p/>
 * 核心改进：
 * 1. 只调用一次 Docker API（ps -a 全量拉取），本地内存过滤匹配容器
 *    避免 ID 查不到再查 name 的二次建连开销
 * 2. 状态判断更精确：区分 running/created/paused/restarting/exited/dead 等状态
 *
 * @date 2025-06-27 11:40:15
 * @author hancher
 * @since 1.0
 */
@Component
@Slf4j
public class DockerHealthChecker extends AbstractHealthChecker {

    @Override
    public String getCheckStrategy() {
        return SupportHeathCheckerEnum.DOCKER_CHECKER.name();
    }

    @Override
    public Result check(NodeConfigDTO node) {
        Optional<CmdProcessor> dockerInstance = getProcessorInstance(ProcessorTypeEnum.DOCKER_CLIENT.name());

        if (dockerInstance.isEmpty()) {
            return Result.fail("未找到docker命令处理器");
        }

        CmdProcessor cmdProcessor = dockerInstance.get();
        CmdParam cmdParam = cmdProcessor.parseCmdParam(node.getProcessCmd());

        if (!(cmdParam instanceof DockerClientCmdParam dockerParam)) {
            return Result.fail("Docker命令参数解析失败");
        }
        if (StringUtils.isBlank(dockerParam.getContainerIdOrName())) {
            return Result.fail("未指定容器ID或名称");
        }
        if (StringUtils.isBlank(dockerParam.getTcpHost())) {
            return Result.fail("未指定docker服务host地址");
        }

        // 只调一次 ps 全量拉取，本地内存中匹配目标容器
        dockerParam.setCmd(DockerClientCmdParam.DockerCmd.ps);
        Result process = cmdProcessor.process(dockerParam);
        if (!process.isSuccess()) {
            return Result.fail("无法获取Docker容器列表");
        }
        if (!(process.getOutput() instanceof List<?> list)) {
            return Result.fail("Docker返回数据格式异常");
        }

        @SuppressWarnings("unchecked")
        List<Container> allContainers = (List<Container>) list;
        String target = dockerParam.getContainerIdOrName();

        // 在本地匹配目标容器（ID 前缀匹配 > 精确名称匹配）
        Container matched = findContainer(allContainers, target);

        if (matched == null) {
            return Result.fail("未找到容器: " + target);
        }

        return evaluateContainerStatus(matched);
    }

    /**
     * 在容器列表中查找目标容器。
     * 匹配策略：先按容器 ID 前缀匹配，再按名称精确匹配。
     */
    private Container findContainer(List<Container> containers, String target) {
        for (Container c : containers) {
            // 按 ID 前缀匹配（支持短 ID）
            if (c.getId() != null && target.length() <= c.getId().length()
                    && c.getId().substring(0, target.length()).equalsIgnoreCase(target)) {
                return c;
            }
        }
        for (Container c : containers) {
            // 按名称精确匹配（去掉名称前的 / 前缀）
            if (c.getNames() != null) {
                for (String name : c.getNames()) {
                    String normalizedName = name.startsWith("/") ? name.substring(1) : name;
                    if (normalizedName.equals(target)) {
                        return c;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据容器状态判断是否健康
     */
    private Result evaluateContainerStatus(Container container) {
        String state = container.getState();
        String status = container.getStatus();
        log.debug("容器状态评估: id={}, state={}, status={}", container.getId(), state, status);

        if ("running".equalsIgnoreCase(state)) {
            if (status != null && status.contains("Up")) {
                return Result.success("容器健康: " + container.getId());
            }
            // running 但 status 不含 "Up"（如 health: starting），也算存活但打 warn
            log.warn("容器状态为 running 但 status 异常: {}", status);
            return Result.success("容器运行中（状态异常）: " + status);
        }

        if ("exited".equalsIgnoreCase(state)) {
            return Result.fail("容器已停止 (exited)");
        }

        if ("dead".equalsIgnoreCase(state)) {
            return Result.fail("容器已死亡 (dead)");
        }

        if ("paused".equalsIgnoreCase(state)) {
            return Result.fail("容器已暂停 (paused)");
        }

        if ("created".equalsIgnoreCase(state) || "restarting".equalsIgnoreCase(state)) {
            log.warn("容器处于中间状态: state={}", state);
            return Result.fail("容器状态异常: " + state);
        }

        return Result.fail("容器状态未知: " + state);
    }
}
