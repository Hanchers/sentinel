package com.hancher.sentinel.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Table("service_cluster")
@Accessors(chain = true)
public class ServiceCluster extends BaseEntity{

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String name;
    private String remark;
    private String status;
    private Integer minAliveNum;
}
