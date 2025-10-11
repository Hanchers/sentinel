package com.hancher.sentinel.web.vo;

import com.hancher.sentinel.enums.DagNodeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 依赖图数据
 * @date 2025-10-10 15:50:53
 * @author hancher
 * @since 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class DagData {

    private List<DagNode> nodes;
    private List<DagEdge> edges;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Data
    public static class DagNode {
        private String id;
        private String label;
        private Integer size;
        private DagNodeTypeEnum nodeType;
        private String color;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @Data
    public static class DagEdge {
        private String source;
        private String target;
    }
}
