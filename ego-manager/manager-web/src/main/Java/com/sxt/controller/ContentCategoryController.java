package com.sxt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxt.service.ContentCategoryService;
import com.sxt.utils.TreeNode;
import com.sxt.vo.EasyUITree;
import com.sxt.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WWF
 * @title: ContentCategoryController
 * @projectName ego
 * @description: com.sxt.controller
 * @date 2019/5/25 20:43
 */
@RestController
@RequestMapping("/content")
public class ContentCategoryController {
    @Reference
    private ContentCategoryService categoryService;


    @GetMapping("/cat/getTree")
    public List<EasyUITree> getTree(@RequestParam(defaultValue = "0") Integer id) {
        List<TreeNode> treeNodes = categoryService.findByPid(id);
        return treeNodes2EasyUITree(treeNodes);
    }

    /**
     * treeNodes2EasyUITree
     *
     * @param treeNodes
     * @return
     */
    private List<EasyUITree> treeNodes2EasyUITree(List<TreeNode> treeNodes) {
        List<EasyUITree> tree = new ArrayList<EasyUITree>();
        for (TreeNode treeNode : treeNodes) {
            EasyUITree easyUITree = new EasyUITree();
            easyUITree.setId(treeNode.getId());
            easyUITree.setText(treeNode.getName());
            easyUITree.setState(treeNode.isParent() ? "closed" : "open");
            tree.add(easyUITree);
        }
        return tree;
    }
    @PostMapping("/cat/saveOrUpdate")
    public Result addNode(Integer parentId,
                          @RequestParam(required = true) String name,Integer id){
        if (parentId!=null){
            categoryService.addNode(parentId,name);
        }
        if (id!=null){
             categoryService.updateNode(id,name);

        }
        return Result.ok();
    }
    @PostMapping("/cat/delete/")
    public Result deleteNode(Integer id){
        System.out.println(id);
        Integer result = categoryService.deleteNode(id);
        if (result>0){
            return Result.ok();
        }else {
            return Result.error();
        }

    }

}
