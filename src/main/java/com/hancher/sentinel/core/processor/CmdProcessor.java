package com.hancher.sentinel.core.processor;

import com.hancher.sentinel.core.processor.dto.CmdParam;
import com.hancher.sentinel.core.processor.dto.Result;

public interface CmdProcessor {
    Result process(CmdParam cmdParam);
}
