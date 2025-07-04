package com.hancher.sentinel.web.param;

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

    private String name;
    private String remark;
    private Integer minAliveNum;
    private List<String> dependClusters;
}
