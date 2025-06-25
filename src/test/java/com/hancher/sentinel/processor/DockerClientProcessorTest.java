package com.hancher.sentinel.processor;

import com.hancher.sentinel.core.processor.CmdProcessor;
import com.hancher.sentinel.core.processor.DockerClientProcessor;
import com.hancher.sentinel.core.dto.DockerClientCmdParam;
import com.hancher.sentinel.core.dto.Result;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

class DockerClientProcessorTest {

    private final CmdProcessor processor = new DockerClientProcessor();




    @Test
    void processPs() {
        long start = System.currentTimeMillis();
        DockerClientCmdParam param = DockerClientCmdParam.builder()
                .tcpHost("tcp://192.168.202.102:2376")
                .certPath("certs")
                .cmd(DockerClientCmdParam.DockerCmd.ps)
                .build();

        Result result = processor.process(param);
        System.out.println(result.getOutput());
        Assert.isTrue(result.isSuccess(), "docker 命令失败");
        System.out.println("耗时：" + (System.currentTimeMillis() - start));
    }
}