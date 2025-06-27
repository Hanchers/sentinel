package com.hancher.sentinel.core.health;

import com.hancher.sentinel.core.dto.NodeConfigDTO;
import com.hancher.sentinel.core.dto.Result;
import com.hancher.sentinel.core.processor.CmdProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 默认的node状态检查器，单条命令直接调用底层的执行器，不需要组合编排
 * @date 2025-06-25 10:41:00
 * @author hancher
 * @since 1.0
 */
@Component
@Slf4j
public class DefaultHealthChecker extends AbstractHealthChecker {

    /**
     * 获取检查策略
     *
     * @return 检查策略
     */
    @Override
    public String getCheckStrategy() {
        return AbstractHealthChecker.DEFAULT_SERVICE_NAME;
    }

    /**
     * 检查节点存活状态
     *
     * @param node 节点参数
     * @return 启动结果
     */
    @Override
    public Result check(NodeConfigDTO node) {
        String processMethod = node.getProcessMethod();
        Optional<CmdProcessor> instance = getProcessorInstance(processMethod);

        if (instance.isEmpty()) {
            log.error("没有找到执行器：{}", processMethod);
            return Result.fail("没有找到执行器:"+processMethod);
        }
        return instance.
                map(cmdProcessor -> cmdProcessor.process(cmdProcessor.parseCmdParam(node.getProcessCmd())))
                .orElse(Result.fail("不支持的执行器类型" + processMethod));
    }
}
