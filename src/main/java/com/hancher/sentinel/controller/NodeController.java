package com.hancher.sentinel.controller;

import com.hancher.sentinel.core.dto.Result;
import com.hancher.sentinel.core.health.HealthCheckerDelegator;
import com.hancher.sentinel.core.starter.NodeStarterDelegator;
import com.hancher.sentinel.entity.ServiceNode;
import com.hancher.sentinel.enums.ServiceNodeStatusEnum;
import com.hancher.sentinel.enums.SupportHeathCheckerEnum;
import com.hancher.sentinel.enums.SupportRestarterEnum;
import com.hancher.sentinel.service.ServiceClusterService;
import com.hancher.sentinel.service.ServiceNodeService;
import com.hancher.sentinel.web.param.NodeParam;
import com.hancher.sentinel.web.vo.PageInfo;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.Objects;

@Validated
@Controller
@RequestMapping("/node")
@AllArgsConstructor
@Slf4j
public class NodeController {

    private ServiceNodeService nodeService;
    private ServiceClusterService clusterService;
    private HealthCheckerDelegator healthCheckerDelegator;
    private NodeStarterDelegator nodeStarterDelegator;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int pageSize,
                       Model model) {
        Page<ServiceNode> pageData = nodeService.page(Page.of(page, pageSize));

        model.addAttribute("pageInfo", PageInfo.of(pageData));
        model.addAttribute("clusterOption", clusterService.listClusterOption(false));
        model.addAttribute("healthCheckerOption", SupportHeathCheckerEnum.listOption());
        model.addAttribute("restarterOption", SupportRestarterEnum.listOption());

        return "node/list";
    }


    @PostMapping("/save")
    public String save(@ModelAttribute("nodeParam") @Valid NodeParam param) {

        ServiceNode node = new ServiceNode();

        BeanUtils.copyProperties(param, node);
        node.setStatus(ServiceNodeStatusEnum.down);
        // 通过，保存依赖
        if (Objects.isNull(node.getId())) {
            nodeService.save(node);
        } else {
            nodeService.updateById(node);
        }


        return "redirect:/node/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        nodeService.removeById(id);
        return  "redirect:/node/list";
    }

    /**
     * 手动触发单节点健康检查
     */
    @PostMapping("/check")
    @ResponseBody
    public Result check(@RequestParam Long id) {
        log.info("健康检查节点【{}】", id);
        ServiceNode node = nodeService.getById(id);
        if (node == null) {
            return Result.fail("节点不存在");
        }
        try {
            Result checkResult = healthCheckerDelegator.checkNode(node);
            node.setStatus(checkResult.isSuccess() ? ServiceNodeStatusEnum.ok : ServiceNodeStatusEnum.down);
            node.setUpdateTime(LocalDateTime.now());
            nodeService.updateById(node);
            return Result.success(checkResult.isSuccess() ? "健康检查通过" : "健康检查失败: " + checkResult.getOutput());
        } catch (Exception e) {
            node.setStatus(ServiceNodeStatusEnum.down);
            node.setUpdateTime(LocalDateTime.now());
            nodeService.updateById(node);
            return Result.fail("健康检查异常: " + e.getMessage());
        }
    }

    /**
     * 手动触发单节点重启
     */
    @PostMapping("/restart")
    @ResponseBody
    public Result restart(@RequestParam Long id) {
        log.info("重启节点【{}】", id);
        ServiceNode node = nodeService.getById(id);
        if (node == null) {
            return Result.fail("节点不存在");
        }
        try {
            Result restartResult = nodeStarterDelegator.restartNode(node);
            node.setStatus(restartResult.isSuccess() ? ServiceNodeStatusEnum.ok : ServiceNodeStatusEnum.down);
            node.setUpdateTime(LocalDateTime.now());
            nodeService.updateById(node);
            return Result.success(restartResult.isSuccess() ? "重启成功" : "重启失败: " + restartResult.getOutput());
        } catch (Exception e) {
            node.setStatus(ServiceNodeStatusEnum.down);
            node.setUpdateTime(LocalDateTime.now());
            nodeService.updateById(node);
            return Result.fail("重启异常: " + e.getMessage());
        }
    }
}
