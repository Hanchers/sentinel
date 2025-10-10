package com.hancher.sentinel.web.param;

import com.hancher.sentinel.enums.ServiceNodeStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 集群节点配置入参
 * @date 2025-07-28 08:30:04
 * @author hancher
 * @since 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class NodeParam {

    private Long id;
    @NotBlank(message = "节点名称不能为空")
    private String name;
    private String remark;
    private ServiceNodeStatusEnum status;
    @NotNull(message = "所属集群不能为空")
    private Long clusterId;
    @NotBlank(message = "节点探活方法不能为空")
    private String healthCheckMethod;
    @NotBlank(message = "节点探活命令不能为空")
    private String healthCheckCmd;
    @NotBlank(message = "节点重启方法不能为空")
    private String restartMethod;
    @NotBlank(message = "节点重启命令不能为空")
    private String restartCmd;
}
