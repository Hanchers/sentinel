package com.hancher.sentinel.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 项目依赖有向无环图
 * @date 2025-06-19 10:06:45
 * @author hancher
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("dependent_dag")
@Accessors(chain = true)
public class DependentDag extends BaseEntity{

    @Id(keyType = KeyType.Auto)
    private Long id;
    private Long sourceClusterId;
    private Long targetClusterId;
}
