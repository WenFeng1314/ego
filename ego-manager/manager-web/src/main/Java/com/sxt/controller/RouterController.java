package com.sxt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面路由的控制器
 * @author WWF
 * @title: RouterController
 * @projectName ego
 * @description: com.sxt.controller
 * @date 2019/5/22 17:34
 */
@Controller
public class RouterController {
    /**
     *
     * @param page
     * @return
     */
    @RequestMapping("/goods/{page}")
    public String routerGoods(@PathVariable("page")String page){
        return "goods/goods-"+page;
    }

    /**
     * 内容路由器
     * @param page
     * @return
     */
    @RequestMapping("/content/{page}")
    public String routerContent(@PathVariable("page")String page){
        return "content/content-"+page;
    }

    /**
     *
     */
    @RequestMapping("/toIndex")
    public String toIndex(){
        return "index";
    }


}
