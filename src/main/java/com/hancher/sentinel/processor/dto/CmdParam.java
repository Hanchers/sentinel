package com.hancher.sentinel.processor.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.util.List;

@Builder
@Accessors(chain = true)
@Data
public class CmdParam {

    private String cmd;
    private List<String> args;
    @Builder.Default
    private Duration timeout = Duration.ofMinutes(10);
}
