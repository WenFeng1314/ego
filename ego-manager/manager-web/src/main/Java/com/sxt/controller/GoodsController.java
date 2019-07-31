package com.sxt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxt.domain.Goods;
import com.sxt.domain.GoodsAttribute;
import com.sxt.model.Page;
import com.sxt.service.GoodsAttributeService;
import com.sxt.service.GoodsCategoryService;
import com.sxt.service.GoodsService;
import com.sxt.utils.DateUtil;
import com.sxt.utils.TreeNode;
import com.sxt.vo.EasyUIDataGrid;
import com.sxt.vo.EasyUITree;
import com.sxt.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 查询商品
 * @author WWF
 * @title: GoodsController
 * @projectName ego
 * @description: com.sxt.controller
 * @date 2019/5/22 19:17
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference//注入代理对象
    private GoodsService goodsService;

    @Reference//注入代理对象
    private GoodsAttributeService goodsAttributeService;

    @Reference//注入代理对象
    private GoodsCategoryService goodsCategoryService;

    /**
     * 查询商品
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/getData")
    public EasyUIDataGrid getGoodsData(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer rows){
        Page<Goods> goodsPage = goodsService.findByPage(page, rows);
        return new EasyUIDataGrid(goodsPage.getTotal(),goodsPage.getResults());

    }

    /**
     * 规格参数查询
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/param/list")
    public EasyUIDataGrid getGoodsParamData(
            @RequestParam(defaultValue = "1")Integer page,
            @RequestParam(defaultValue = "10")Integer rows){
        Page<GoodsAttribute> attributePage = goodsAttributeService.findByPage(page, rows);
        return  new EasyUIDataGrid(attributePage.getTotal(),attributePage.getResults());

    }
    /**
     * 新增商品
     */
    @PostMapping("/cat/list")
    public List<EasyUITree> getTree(@RequestParam(defaultValue = "0") Integer id){
        List<TreeNode> treeNodes = goodsCategoryService.findByPid(id);
         return convertTreeNode(treeNodes);
    }

    /**
     * 返回值是通用的树，复杂的，抽象的
     * 	而jsp 需要easyUi的树：简单的，具体的
     * @param treeNodes
     * @return
     */
    private List<EasyUITree> convertTreeNode(List<TreeNode> treeNodes) {
        List<EasyUITree> easyUITrees=new ArrayList<EasyUITree>();
        for (TreeNode treeNode:treeNodes){
            easyUITrees.add(treeEasyTree(treeNode));
        }
        return  easyUITrees;
    }

    /**
     *  将复杂的树转换为具体的树
     * @param treeNode
     * @return
     */
    private EasyUITree treeEasyTree(TreeNode treeNode) {
        EasyUITree easyUITree = new EasyUITree();
        easyUITree.setId(treeNode.getId());
        easyUITree.setText(treeNode.getName());
        easyUITree.setState(treeNode.isParent()?"closed":"open");
        return easyUITree;

    }

    /**
     * 批量下架
     * @param ids
     * @return
     */
    @PostMapping("/instock")
    public Object batchDown(@RequestParam(required = true) String ids){
        Map<String,Object> map=new HashMap<String, Object>();
        Integer integer = goodsService.batchDown(ids);
        if (integer>0){
            map.put("status",200);
        }else {
            map.put("status",400);
        }
        return map;
    }

    /**
     * 商品上架
     * @return
     */
    @PostMapping("/reshelf")
    public Object isOnSale(@RequestParam(required = true) String ids){
        Map<String,Object> map=new HashMap<String, Object>();
        Integer integer = goodsService.batchIsOnSale(ids);
        if (integer>0){
            map.put("status",200);
        }else {
            map.put("status",400);
        }

        return map;
    }
    /**
     *
     */
    @PostMapping("/delete")
    public Object batchDeleteGoods(String ids){
        Map<String,Object> map=new HashMap<String, Object>();
        Integer integer = goodsService.batchDeleteGoods(ids);
        if (integer>0){
            map.put("status",200);
        }else {
            map.put("status",400);
        }
          return map;
    }

    /**
     * 查询商品的描述
     * @param id
     * @return
     */
    @GetMapping("/desc/{id}")
    public Object queryGoodsDesc(@PathVariable("id")Integer id){
        Map<String,Object> map=new HashMap<String, Object>();
        Goods goods = goodsService.findById(id);
        if (null!=goods){
            map.put("status",200);
            map.put("data",goods.getGoodsContent());
            return  map;
        }else {
            map.put("status",400);
            return map;
        }

    }
    /**
     * 编辑商品
     * @param goods
     * @return
     */
    @PostMapping("/update")
    public Object updateGoods(Goods goods){
         Map<String,Object> map=new HashMap<String, Object>();
        Goods update = goodsService.update(goods);
        if(null!=update){
            map.put("status",200);
            return map;
        }else {
            map.put("status",400);
            return  map;
        }



    }
    @PostMapping("/save")
    public Result saveGoods(Goods goods){
        goods.setIsOnSale(true);
        goods.setIsNew(true);
        goods.setIsHot(true);

        goods.setOnTime(Integer.valueOf(DateUtil.getTadayDate("yyyyMMdd")));
        goods.setLastUpdate(Integer.valueOf(DateUtil.getTadayDate("MMddHHmmss")));
        goods.setClickCount(1);
        goods.setIsReal((byte)1);
        goods.setUpdateTime(new Date());
        Integer result = goodsService.add(goods);
        if (result>0){
            return Result.ok();
        }else {
            return Result.error();
        }


    }
    /**
     * 新增规格参数
     */
    @PostMapping("/param/save/{typeId}")
    public  Result addParam(@PathVariable("typeId")Integer typeId,GoodsAttribute goodsAttribute){
            goodsAttribute.setTypeId(typeId.shortValue());
        Integer result = goodsAttributeService.add(goodsAttribute);
        if (result>0){
            return  Result.ok();
        }else {
            return Result.error();
        }
    }
    @PostMapping("/param/delete")
    public Result deleteParam(
            @RequestParam(required = true) String ids){
        Integer result = goodsAttributeService.batchDeleteAttributeParam(ids);
        if (result>0){
            return  Result.ok();
        }else {
            return Result.error();
        }

    }


}
