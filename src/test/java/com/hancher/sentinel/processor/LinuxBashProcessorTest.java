package com.hancher.sentinel.processor;

import com.hancher.sentinel.processor.dto.BashCmdParam;
import com.hancher.sentinel.processor.dto.CmdParam;
import com.hancher.sentinel.processor.dto.Result;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.List;

class LinuxBashProcessorTest {

    private CmdProcessor processor = new LinuxBashLineProcessor();


    @Test
    void processPing() {
        long start = System.currentTimeMillis();
        CmdParam param = BashCmdParam.builder().cmd("ping").args(List.of("-c", "4", "www.baidu.com")).build();
        Result result = processor.process(param);
        System.out.println(result.getOutput());
        Assert.isTrue(result.isSuccess(), "ping失败");
        System.out.println("耗时：" + (System.currentTimeMillis() - start));
    }
}