package com.hancher.sentinel.core.processor;

import com.hancher.sentinel.core.processor.dto.CmdParam;
import com.hancher.sentinel.core.processor.dto.Result;
import com.hancher.sentinel.enums.ProcessorTypeEnum;

/**
 * 命令执行器
 * @date 2025-06-20 14:25:17
 * @author hancher
 * @since 1.0
 */
public interface CmdProcessor {

    /**
     * 支持的命令类型
     * @return 命令类型
     */
    ProcessorTypeEnum supportType();

    /**
     * 执行命令
     * @param cmdParam 命令参数
     * @return 执行结果
     */
    Result process(CmdParam cmdParam);


}
