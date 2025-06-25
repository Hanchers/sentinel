package com.hancher.sentinel.core.starter;

import com.hancher.sentinel.core.dto.NodeConfigDTO;
import com.hancher.sentinel.core.dto.Result;
import com.hancher.sentinel.core.processor.AbstractCmdProcessor;
import com.hancher.sentinel.core.processor.CmdProcessor;
import com.hancher.sentinel.enums.ProcessorTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 默认的node启动器，单条命令直接调用底层的执行器，不需要组合编排
 * @date 2025-06-25 10:41:00
 * @author hancher
 * @since 1.0
 */
@Component(AbstractNodeStarter.DEFAULT_SERVICE_NAME)
@Slf4j
public class DefaultNodeStarter extends AbstractNodeStarter {
    /**
     * 服务节点实际启动方法
     *
     * @param node 节点参数
     * @return 启动结果
     */
    @Override
    public Result restart(NodeConfigDTO node) {
        String processorType = node.getProcessMethod();
        Optional<ProcessorTypeEnum> anEnum = ProcessorTypeEnum.getByName(processorType);
        if (anEnum.isEmpty()) {
            log.error("不支持的执行器类型：{}", processorType);
            return Result.fail("不支持的执行器类型:"+processorType);
        }

        Optional<CmdProcessor> instance = AbstractCmdProcessor.getInstance(anEnum.get());

        if (instance.isEmpty()) {
            log.error("没有找到执行器：{}", anEnum);
            return Result.fail("没有找到执行器:"+anEnum);
        }
        return instance.
                map(cmdProcessor -> cmdProcessor.process(cmdProcessor.parseCmdParam(node.getProcessCmd())))
                .orElse(Result.fail("不支持的执行器类型" + processorType));
    }
}
