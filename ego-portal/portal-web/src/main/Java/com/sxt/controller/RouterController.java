package com.sxt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author WWF
 * @title: RouterController
 * @projectName ego
 * @description: com.sxt.controller
 * @date 2019/5/27 12:38
 */
@Controller
public class RouterController {


    /**
     * 页面的路由
     *
     * @return
     */
    @GetMapping("/cart/list")
    public String rounterCartList() {
        return "cart-list";

    }

    /**
     * 拦截的路由
     */
    @GetMapping("/toIndex")
    public String toIndex() {
        return "index";
    }
}
