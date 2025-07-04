package com.hancher.sentinel.entity;

import com.hancher.sentinel.enums.ServiceClusterStatusEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.CommaSplitTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 服务集群
 * @date 2025-06-19 10:05:56
 * @author hancher
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("service_cluster")
@Accessors(chain = true)
public class ServiceCluster extends BaseEntity{

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String name;
    private String remark;
    private ServiceClusterStatusEnum status;
    private Integer minAliveNum;
    @Column(typeHandler = CommaSplitTypeHandler.class)
    private List<String> dependClusters;
}
