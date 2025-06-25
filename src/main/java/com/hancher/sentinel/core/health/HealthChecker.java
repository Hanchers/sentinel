package com.hancher.sentinel.core.health;

import com.hancher.sentinel.core.dto.NodeConfigDTO;
import com.hancher.sentinel.core.dto.Result;

/**
 * 服务节点存活检查器<br>
 * 因为processor的定位是底层命令层组件，健康检查有时可能需要多个命令的组合，所以有了这个检查器。<br>
 * 存活检查器默认可以直接调用底层命令执行器，也可以自己实现一个检查器，会优先匹配自定义的检查器
 * @date 2025-06-25 10:00:03
 * @author hancher
 * @since 1.0
 */
public interface HealthChecker {

    /**
     * 检查服务节点是否存活
     * @param node 服务配置
     * @return 结果
     */
    Result check(NodeConfigDTO node);
}
