package com.hancher.sentinel.core.processor;

import com.hancher.sentinel.core.dto.BashCmdParam;
import com.hancher.sentinel.core.dto.CmdParam;
import com.hancher.sentinel.core.dto.Result;
import com.hancher.sentinel.enums.ProcessorTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author hancher
 * @date 2025/06/10
 */
@Component
@Slf4j
public class LinuxBashLineProcessor extends AbstractCmdProcessor {

    // 白名单
    Set<String> whiteCmdSet = Set.of("ping","ls","java","docker");

    /**
     * 支持的命令类型
     *
     * @return 命令类型
     */
    @Override
    public ProcessorTypeEnum supportType() {
        return ProcessorTypeEnum.BASH;
    }

    /**
     * 解析命令参数
     *
     * @param param 命令行： ping -c 5 192.168.1.1
     * @return 解析结果
     */
    @Override
    public CmdParam parseCmdParam(String param) {
        Assert.hasText(param, "bash 命令不能为空");
        String cmd = null;
        List<String> args = new ArrayList<>();
        String[] split = param.trim().split(" ");
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                cmd = split[i];
            } else if (StringUtils.isNotBlank(split[i])){
                args.add(split[i]);
            }
        }

        return BashCmdParam.builder()
                .cmd(cmd)
                .args(args)
                .build();

    }

    @Override
    public Result process(CmdParam param) {
        if (!(param instanceof BashCmdParam cmdParam)) {
            return Result.fail("参数错误");
        }

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
