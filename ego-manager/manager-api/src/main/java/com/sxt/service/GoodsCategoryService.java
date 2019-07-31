package com.sxt.service;

import com.sxt.utils.TreeNode;

import java.util.List;

/**
 * @author WWF
 * @title: GoodsCategoryService
 * @projectName ego
 * @description: com.sxt.service
 * @date 2019/5/22 21:10
 */
public interface GoodsCategoryService  {
    //通过父id,查询查询它的子菜单
    List<TreeNode> findByPid(Integer pid);

    //一次性加载所有的树
    List<TreeNode> loadAllTree();

}
