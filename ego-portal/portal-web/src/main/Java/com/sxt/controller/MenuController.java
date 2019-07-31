package com.sxt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxt.service.GoodsCategoryService;
import com.sxt.utils.TreeNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author WWF
 * @title: MenuController
 * @projectName ego
 * @description: com.sxt.controller
 * @date 2019/5/27 8:17
 */
@RestController
public class MenuController {
    @Reference
    private GoodsCategoryService goodsCategoryService;
    @GetMapping("/goods/cat/getAllMenu")
    public List<TreeNode> getAllMenu(){
        List<TreeNode> treeNodes = goodsCategoryService.loadAllTree();
        return treeNodes;

    }
}
