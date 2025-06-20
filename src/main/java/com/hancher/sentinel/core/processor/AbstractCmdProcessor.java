package com.hancher.sentinel.core.processor;

import com.hancher.sentinel.enums.ProcessorTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 系统默认命令执行器
 * @date 2025-06-20 14:27:51
 * @author hancher
 * @since 1.0
 */
@Slf4j
public  abstract class AbstractCmdProcessor implements InitializingBean, CmdProcessor {

    /**
     * 实例
     */
    private static final Map<ProcessorTypeEnum, CmdProcessor> instanceMap = new HashMap<>();

    /**
     * 注册
     * @param channel 渠道
     * @param alertChannel 实例
     */
    protected void register(ProcessorTypeEnum channel, CmdProcessor alertChannel) {
        instanceMap.put(channel, alertChannel);
    }

    public static Optional<CmdProcessor> getInstance(ProcessorTypeEnum channel) {
        return Optional.ofNullable(instanceMap.get(channel));
    }


    /**
     * 自动注册实例
     */
    @Override
    public void afterPropertiesSet() {
        this.register(supportType(), this);
    }
}
