package com.hancher.sentinel.processor;

import com.hancher.sentinel.processor.dto.CmdParam;
import com.hancher.sentinel.processor.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * @author hancher
 * @date 2025/06/10
 */
@Slf4j
public class LinuxBashLineProcessor implements CmdProcessor {

    // 白名单
    Set<String> whiteCmdSet = Set.of("ping","ls","java","docker");


    @Override
    public Result process(CmdParam cmdParam) {
        String cmd = cmdParam.getCmd();
        List<String> args = cmdParam.getArgs();

        Assert.notEmpty(args, "参数不能为空");
        if (!whiteCmdSet.contains(cmd)) {
            return Result.fail("禁止执行%s命令".formatted(cmd));
        }

        // cmd
        CommandLine cmdLine = new CommandLine(cmd);
        cmdLine.addArguments(args.toArray(new String[0]));

        log.info("执行命令：{}", cmdLine);
        // 执行器
        DefaultExecutor executor = DefaultExecutor.builder()
                .get();
        executor.setExitValue(0);

        ExecuteWatchdog watchdog = ExecuteWatchdog.builder().setTimeout(cmdParam.getTimeout()).get();
        executor.setWatchdog(watchdog);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(baos);
        executor.setStreamHandler(streamHandler);
        int exitCode = -1;
        String output;
        try {
            exitCode = executor.execute(cmdLine);
            output = baos.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            output = e.getMessage();
            if (e instanceof ExecuteException ee)  {
                exitCode = ee.getExitValue();
            }
            log.error("执行命令失败：exitCode={}", exitCode, e);
        }

        if (exitCode != 0) {
            return Result.fail(output);
        }

        return Result.success(output);
    }
}
