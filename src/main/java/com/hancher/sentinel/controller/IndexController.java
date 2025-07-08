package com.hancher.sentinel.controller;

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
public class IndexController {


    @GetMapping("")
    public String index(Model model) {
        System.out.println("index");
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
    public String home() {
        return "home";
    }



}
