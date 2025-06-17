package com.hancher.sentinel.processor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@Accessors(chain = true)
@Data
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



    public static enum DockerCmd {
        /**
         * 获取所有容器
         */
        ps,
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
