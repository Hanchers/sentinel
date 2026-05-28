package com.hancher.sentinel.controller;

import com.hancher.sentinel.enums.ServiceClusterStatusEnum;
import com.hancher.sentinel.enums.ServiceNodeStatusEnum;
import com.hancher.sentinel.service.ServiceClusterService;
import com.hancher.sentinel.service.ServiceNodeService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页
 * @date 2025-07-07 10:23:53
 * @author hancher
 * @since 1.0
 */
@Controller
@RequestMapping("/index")
@AllArgsConstructor
public class IndexController {

    private final ServiceClusterService clusterService;
    private final ServiceNodeService nodeService;


    @GetMapping("")
    public String index(Model model) {
        List<Map<String, Object>> menus = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Map<String, Object> menu = new HashMap<>();
            menu.put("menuName", "菜单" + i);
            menu.put("url", "菜单" + i);
            menu.put("target", "菜单" + i);
            menu.put("isRefresh", i/2+"");
            menu.put("children", List.of());
            menus.add(menu);
        }


        model.addAttribute("menus", menus);
        model.addAttribute("user", new HashMap<>());
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        // 集群总数
        long clusterCount = clusterService.count();
        // 节点总数
        long nodeCount = nodeService.count();
        // 下线集群（状态为 down 的集群）
        long downClusterCount = clusterService.listByStatus(ServiceClusterStatusEnum.down).size();
        // 下线节点（状态为 down 的节点）
        long downNodeCount = nodeService.count(
                QueryWrapper.create().where("status = ?", ServiceNodeStatusEnum.down));

        model.addAttribute("clusterCount", clusterCount);
        model.addAttribute("nodeCount", nodeCount);
        model.addAttribute("downCluster", downClusterCount);
        model.addAttribute("downNode", downNodeCount);

        return "home";
    }



}
