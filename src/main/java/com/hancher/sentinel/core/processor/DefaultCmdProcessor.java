package com.hancher.sentinel.core.processor;

import com.hancher.sentinel.core.processor.dto.Result;
import com.hancher.sentinel.entity.ServiceNode;
import com.hancher.sentinel.enums.ProcessorTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 系统默认命令执行器
 *
 * @author hancher
 * @date 2025-06-20 14:27:51
 * @since 1.0
 */
@Slf4j
@Component
public class DefaultCmdProcessor {

    /**
     * 存活检查
     *
     * @param node 服务器节点
     * @return 存活检查结果
     */
    public Result healthCheck(ServiceNode node) {

        Optional<CmdProcessor> instance = getInstance(node.getHealthCheckMethod());
        return instance.
                map(cmdProcessor -> cmdProcessor.process(cmdProcessor.parseCmdParam(node.getHealthCheckCmd())))
                .orElse(Result.fail("不支持的执行器类型" + node.getHealthCheckMethod()));

    }


    /**
     * 获取执行器实例
     *
     * @param processorType 执行器类型
     * @return 执行器实例
     */
    private Optional<CmdProcessor> getInstance(String processorType) {
        Optional<ProcessorTypeEnum> anEnum = ProcessorTypeEnum.getByName(processorType);
        if (anEnum.isEmpty()) {
            log.error("不支持的执行器类型：{}", processorType);
            return Optional.empty();
        }

        Optional<CmdProcessor> instance = AbstractCmdProcessor.getInstance(anEnum.get());

        if (instance.isEmpty()) {
            log.error("没有找到执行器：{}", anEnum);
            return instance;
        }
        return instance;
    }


    /**
     * 重启
     *
     * @param node 节点
     * @return 重启结果
     */
    public Result restart(ServiceNode node) {
        Optional<CmdProcessor> instance = getInstance(node.getRestartMethod());
        return instance.
                map(cmdProcessor -> cmdProcessor.process(cmdProcessor.parseCmdParam(node.getRestartCmd())))
                .orElse(Result.fail("不支持的执行器类型" + node.getRestartMethod()));
    }


}
