package com.hancher.sentinel.controller;

import com.hancher.sentinel.service.ServiceClusterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.ArrayList;

@Controller
@RequestMapping("/cluster")
public class ServiceClusterController {

    @Autowired
    private ServiceClusterService clusterService;


    @GetMapping
    public String list(Model model) {
        model.addAttribute("serviceClusters", clusterService.list());
        return "cluster/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        return "cluster/form";
    }

    @PostMapping
    public String save() {

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
