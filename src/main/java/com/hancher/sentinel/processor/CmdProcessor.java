package com.hancher.sentinel.processor;

import com.hancher.sentinel.processor.dto.CmdParam;
import com.hancher.sentinel.processor.dto.Result;

import java.util.List;

public interface CmdProcessor {
    Result process(CmdParam cmdParam);
}
