package com.hancher.sentinel.controller;

import com.hancher.sentinel.core.dag.InnerClusterDag;
import com.hancher.sentinel.entity.ServiceCluster;
import com.hancher.sentinel.enums.DagNodeEnum;
import com.hancher.sentinel.enums.ServiceClusterStatusEnum;
import com.hancher.sentinel.service.ServiceClusterService;
import com.hancher.sentinel.web.param.ClusterParam;
import com.hancher.sentinel.web.vo.SentinelKey;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Validated
@Controller
@RequestMapping("/cluster")
@AllArgsConstructor
public class ServiceClusterController {

    private ServiceClusterService clusterService;
    private InnerClusterDag innerClusterDag;


    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("serviceClusters", clusterService.list());

        List<ServiceCluster> list = clusterService.list();
        List<SentinelKey> clusterOption = new ArrayList<>();
        clusterOption.add(SentinelKey.builder().value(DagNodeEnum.start.getCode() + "").text("开始节点").build());
        for (ServiceCluster serviceCluster : list) {
            clusterOption.add(SentinelKey.builder().value(serviceCluster.getId() + "").text(serviceCluster.getName()).build());
        }

        model.addAttribute("clusterOption", clusterOption);
        return "cluster/list";
    }


    @PostMapping("/save")
    public String save(@ModelAttribute("clusterParam") @Valid ClusterParam param) {
        ServiceCluster cluster = new ServiceCluster();

        BeanUtils.copyProperties(param, cluster);
        cluster.setStatus(ServiceClusterStatusEnum.down);
        // 检查环
        innerClusterDag.checkCircleWhenAddNode(cluster);
        // 通过，保存依赖
        if (Objects.isNull(cluster.getId())) {
            clusterService.save(cluster);
        } else {
            clusterService.updateById(cluster);
        }

        return "redirect:/cluster/list";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("clusterParam") @Valid ClusterParam param) {
        return  "redirect:/cluster/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        clusterService.removeById(id);
        return  "redirect:/cluster/list";
    }
}
