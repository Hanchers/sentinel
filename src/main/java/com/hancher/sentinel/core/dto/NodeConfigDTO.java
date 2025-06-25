package com.hancher.sentinel.core.dto;

import lombok.Data;
/**
 * Node节点的配置信息 到 process处理的中转防腐层
 * @date 2025-06-25 11:43:47
 * @author hancher
 * @since 1.0
 */
@Data
public class NodeConfigDTO {
    private String processMethod;
    private String processCmd;


    /**
     * 创建Node节点配置信息
     * @param processMethod 处理类型
     * @param processCmd 处理命令
     * @return 配置类
     */
    public static NodeConfigDTO of(String processMethod, String processCmd) {
        NodeConfigDTO nodeConfigDTO = new NodeConfigDTO();
        nodeConfigDTO.setProcessMethod(processMethod);
        nodeConfigDTO.setProcessCmd(processCmd);
        return nodeConfigDTO;
    }
}
