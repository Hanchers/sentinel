package com.hancher.sentinel.core.processor;

import com.hancher.sentinel.core.processor.dto.CmdParam;
import com.hancher.sentinel.core.processor.dto.Result;
import com.hancher.sentinel.enums.ProcessorTypeEnum;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

/**
 * 命令执行器
 * @date 2025-06-20 14:25:17
 * @author hancher
 * @since 1.0
 */
@Validated
public interface CmdProcessor {

    /**
     * 支持的命令类型
     * @return 命令类型
     */
    ProcessorTypeEnum supportType();

    /**
     * 解析命令参数
     * @param param 命令参数
     * @return 解析结果
     */
    CmdParam parseCmdParam(String param);

    /**
     * 执行命令
     * @param cmdParam 命令参数
     * @return 执行结果
     */
    Result process(@Valid CmdParam cmdParam);


}
