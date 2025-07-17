package com.hancher.sentinel.web.param;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 集群配置入参
 * @date 2025-07-04 08:30:04
 * @author hancher
 * @since 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class ClusterParam {

    private Long id;
    @NotBlank(message = "集群名称不能为空")
    private String name;
    private String remark;
    @NotNull(message = "最小存活数不能为空")
    @Min(value = 1, message = "最小存活数不能小于1")
    private Integer minAliveNum;
    @NotEmpty(message = "上游集群不能为空")
    private List<String> dependClusters;
}
