package com.hancher.sentinel.controller;

import com.hancher.sentinel.entity.ServiceCluster;
import com.hancher.sentinel.enums.DagNodeEnum;
import com.hancher.sentinel.service.ServiceClusterService;
import com.hancher.sentinel.web.param.ClusterParam;
import com.hancher.sentinel.web.vo.SentinelKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cluster")
public class ServiceClusterController {

    @Autowired
    private ServiceClusterService clusterService;


    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("serviceClusters", clusterService.list());
        return "cluster/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        List<ServiceCluster> list = clusterService.list();
        List<SentinelKey> clusterOption = new ArrayList<>();
        clusterOption.add(SentinelKey.builder().value(DagNodeEnum.start.getCode() + "").text("开始节点").build());
        for (ServiceCluster serviceCluster : list) {
            clusterOption.add(SentinelKey.builder().value(serviceCluster.getId() + "").text(serviceCluster.getName()).build());
        }

        model.addAttribute("clusterOption", clusterOption);

        return "cluster/form_new";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ClusterParam param) {
        System.out.println(param);
        return "redirect:/cluster";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        return "cluster/form";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        return "redirect:/cluster";
    }
}
