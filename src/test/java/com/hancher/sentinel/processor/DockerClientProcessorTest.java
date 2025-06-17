package com.hancher.sentinel.processor;

import com.hancher.sentinel.processor.dto.CmdParam;
import com.hancher.sentinel.processor.dto.DockerClientCmdParam;
import com.hancher.sentinel.processor.dto.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.List;

class DockerClientProcessorTest {

    private final CmdProcessor processor = new DockerClientProcessor();




    @Test
    void processPs() {
        long start = System.currentTimeMillis();
        DockerClientCmdParam param = DockerClientCmdParam.builder()
                .tcpHost("tcp://192.168.1.2:2376")
                .certPath("temp/certs")
                .cmd(DockerClientCmdParam.DockerCmd.ps)
                .build();

        Result result = processor.process(param);
        System.out.println(result.getOutput());
        Assert.isTrue(result.isSuccess(), "docker 命令失败");
        System.out.println("耗时：" + (System.currentTimeMillis() - start));
    }
}