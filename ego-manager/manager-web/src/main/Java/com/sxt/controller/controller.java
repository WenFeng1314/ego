package com.sxt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxt.domain.Goods;
import com.sxt.model.Page;
import com.sxt.service.GoodsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author WWF
 * @title: controller
 * @projectName ego
 * @description: com.sxt
 * @date 2019/5/21 20:40
 */
@RestController
@RequestMapping("/goods")
public class controller {
    //注入代理对象
    @Reference
    private GoodsService goodsService;
    @RequestMapping("/queryGoods")
    public Object queryGoods(int page,int size){
        Page<Goods> pageData = goodsService.findByPage(page, size);
        System.out.println(pageData);
        // 将对象->json jackson,已经依赖了
        return pageData;
    }
}
