package com.hancher.sentinel.core.health;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.hancher.sentinel.core.processor.AbstractCmdProcessor;
import com.hancher.sentinel.core.processor.CmdProcessor;
import com.hancher.sentinel.enums.ProcessorTypeEnum;
import com.hancher.sentinel.exception.SentinelRunException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * 节点检查器超类
 *
 * @author hancher
 * @date 2025-06-25 10:21:46
 * @since 1.0
 */
@Slf4j
public abstract class AbstractHealthChecker implements HealthChecker {

    /**
     * 系统默认处理器
     */
    public static final String DEFAULT_SERVICE_NAME = "defaultHealthChecker";

    @Resource
    protected JsonMapper jsonMapper;
    /**
     * 获取处理器实例
     */
    protected Optional<CmdProcessor> getProcessorInstance(String processorType ) {
        Optional<ProcessorTypeEnum> anEnum = ProcessorTypeEnum.getByName(processorType);
        if (anEnum.isEmpty()) {
            log.error("不支持的执行器类型：{}", processorType);
            return Optional.empty();
        }

        return AbstractCmdProcessor.getInstance(anEnum.get());
    }

    /**
     * 解析json
     * @param json json
     * @param clazz 类
     * @param <T> 泛型
     * @return 结果
     */
    protected <T> T parseJson(String json, Class<T> clazz) {
        try {
            return jsonMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("json解析异常", e);
            throw new SentinelRunException("json解析异常");
        }
    }
}
