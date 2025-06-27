package com.hancher.sentinel.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Builder
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DockerClientCmdParam extends CmdParam {

    /**
     * tcp://192.168.1.1:2376
     */
    @NotBlank(message = "docker服务tcp地址不能为空")
    private String tcpHost;
    /**
     * docker 客户端证书路径：须有ca.pem, client-cert.pem, client-key.pem
     */
    @NotBlank(message = "docker服务tcp客户端证书地址不能为空")
    private String certPath;
    @Builder.Default
    private Duration timeout = Duration.ofMinutes(10);

    /**
     * docker 命令
     */
    @NotNull(message = "docker 命令不能为空")
    private DockerCmd cmd;

    private String containerIdOrName;

    /**
     * docker 命令参数
     */
    private Map<String, String> args = new HashMap<>();


    public static enum DockerCmd {
        /**
         * 获取所有容器
         */
        ps,
        ps_filter,
        /**
         * 启动容器
         */
        start,
        /**
         * 停止容器
         */
        stop,

    }
}
