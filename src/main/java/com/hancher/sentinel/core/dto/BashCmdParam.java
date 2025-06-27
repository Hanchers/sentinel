package com.hancher.sentinel.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BashCmdParam  extends CmdParam{

    @NotBlank(message = "bash命令不能为空")
    private String cmd;
    @NotEmpty(message = "bash命令参数不能为空")
    private List<String> args;
    @Builder.Default
    private Duration timeout = Duration.ofMinutes(10);
}
