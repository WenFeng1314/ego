package com.sxt.service;


import com.sxt.domain.ContentCategory;
import com.sxt.utils.TreeNode;

import java.util.List;

/**
 * @author WWF
 * @title: GoodsCategoryService
 * @projectName ego
 * @description: com.sxt.service
 * @date 2019/5/22 21:10
 */
public interface ContentCategoryService  {
    /**
     * 根据pid查询子菜单
     * @param pid
     * @return
     */
    List<TreeNode> findByPid(Integer pid);

    /**
     * 新增一个节点
     * @param pid
     * @param name
     */
    void addNode(Integer pid,String name);

    /**
     * 修改一个节点
     * @param id
     * @param name
     */
    void updateNode(Integer id,String name);

    /**
     * 删除一个节点
     * @param id
     */
    Integer deleteNode(Integer id);

    /**
     * 一次性加载所有的树
     * @return
     */
    List<TreeNode> loadAllTree();

}
