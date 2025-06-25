package com.hancher.sentinel.core.starter;

import com.hancher.sentinel.core.dto.NodeConfigDTO;
import com.hancher.sentinel.core.dto.Result;

/**
 * 服务节点启动器<br>
 * 因为processor的定位是底层命令层组件，节点启动是有时可能需要多个命令的组合，所以有了这个启动器。<br>
 * 启动器默认可以直接调用底层命令执行器，也可以自己实现一个检查器，会优先匹配自定义的检查器
 * @date 2025-06-25 10:00:03
 * @author hancher
 * @since 1.0
 */
public interface NodeStarter {

    /**
     * 启动服务节点
     * @param node 节点参数
     * @return 启动结果
     */
    Result restart(NodeConfigDTO node);
}
