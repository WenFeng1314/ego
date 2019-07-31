package com.sxt.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.sxt.domain.GoodsCategory;
import com.sxt.domain.GoodsCategoryExample;
import com.sxt.mapper.GoodsCategoryMapper;
import com.sxt.service.GoodsCategoryService;
import com.sxt.utils.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WWF
 * @title: GoodsCategoryServiceImpl
 * @projectName ego
 * @description: com.sxt.service.impl
 * @date 2019/5/22 21:29
 */
@Service
public class GoodsCategoryServiceImpl implements GoodsCategoryService {
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;
    @Autowired
    private JedisPool jedisPool;
    private static final String MENU_KEY="EGO-SHOP-ALL-MENU";

    @Override
    public List<TreeNode> findByPid(Integer pid) {
        GoodsCategoryExample goodsCategoryExample = new GoodsCategoryExample();
         GoodsCategoryExample.Criteria criteria = goodsCategoryExample.createCriteria();
         criteria.andParentIdEqualTo(pid.shortValue());
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.selectByExample(goodsCategoryExample);
        List<TreeNode> treeNodes=new ArrayList<TreeNode>();
        for (GoodsCategory goodsCategory:goodsCategories){
            TreeNode node = goodsCate2TreeNode(goodsCategory);
            treeNodes.add(node);
        }


        return treeNodes;
    }

    /**
     *
     * @param goodsCategory
     * @return
     */
    private TreeNode goodsCate2TreeNode(GoodsCategory goodsCategory) {
        TreeNode node = new TreeNode();
        node.setId(goodsCategory.getId().toString());
        node.setName(goodsCategory.getName());
        node.setParent(goodsCategory.getLevel()==(short)3?false:true);
        node.setIcon(goodsCategory.getImage());
        //设置其他属性
        Map<String,Object> attributes=new HashMap<String, Object>();
        attributes.put("mobile_name",goodsCategory.getMobileName());
        attributes.put("tree_path",goodsCategory.getParentId());
        attributes.put("is_hot",goodsCategory.getIsHot());
        node.setAttributes(attributes);
        return  node;
    }

    /**
     * 一次性加载所有的树
     * @return
     */
    @Override
    public List<TreeNode> loadAllTree() {
        Jedis jedis = jedisPool.getResource();
        List<TreeNode> treeNodes=null;
        try {
            if (jedis.exists(MENU_KEY)){//从缓存里面加载
                String json = jedis.get(MENU_KEY);
                treeNodes = JSON.parseArray(json, TreeNode.class);

            }else {//从数据库里面查询
                treeNodes = loadAllTree((short) 0);
                //将数据放入缓存里面
                jedis.set(MENU_KEY,JSON.toJSONString(treeNodes));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return treeNodes;
    }
    // 我们的树里面包含完整的子节点
    public List<TreeNode> loadAllTree(Short id) {
        GoodsCategoryExample goodsCategoryExample = new GoodsCategoryExample();
             goodsCategoryExample.createCriteria().andParentIdEqualTo(id);
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.selectByExample(goodsCategoryExample);
        // 是一级节点
        List<TreeNode> trees=new ArrayList<TreeNode>();
        for (GoodsCategory goodsCategory : goodsCategories) {
            TreeNode treeNode = goodsCate2TreeNode(goodsCategory);
            //开始递归了
            treeNode.setChildren(loadAllTree(goodsCategory.getId()));
            // 将子节点也加载进去
            trees.add(treeNode);
        }
        return trees;

    }



}
