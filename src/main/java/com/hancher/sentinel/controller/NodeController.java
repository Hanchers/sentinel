package com.hancher.sentinel.controller;

import com.hancher.sentinel.entity.ServiceNode;
import com.hancher.sentinel.enums.ServiceNodeStatusEnum;
import com.hancher.sentinel.enums.SupportHeathCheckerEnum;
import com.hancher.sentinel.enums.SupportRestarterEnum;
import com.hancher.sentinel.service.ServiceClusterService;
import com.hancher.sentinel.service.ServiceNodeService;
import com.hancher.sentinel.web.param.NodeParam;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Validated
@Controller
@RequestMapping("/node")
@AllArgsConstructor
public class NodeController {

    private ServiceNodeService nodeService;
    private ServiceClusterService clusterService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int pageSize,
                       Model model) {
        Page<ServiceNode> pageData = nodeService.page(Page.of(page, pageSize));

        model.addAttribute("nodeList", pageData.getRecords());
        model.addAttribute("rows", pageData.getTotalRow());
        model.addAttribute("pages", pageData.getTotalPage());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
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
}
