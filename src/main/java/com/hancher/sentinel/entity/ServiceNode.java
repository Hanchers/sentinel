package com.hancher.sentinel.entity;

import com.hancher.sentinel.enums.ServiceClusterStatusEnum;
import com.hancher.sentinel.enums.ServiceNodeStatusEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * 服务节点
 * @date 2025-06-19 10:05:46
 * @author hancher
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("service_node")
@Accessors(chain = true)
public class ServiceNode extends BaseEntity {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String name;
    private String remark;
    private ServiceNodeStatusEnum status;
    private Long clusterId;
    private String healthCheckMethod;
    private String healthCheckCmd;
    private String restartMethod;
    private String restartCmd;
}
