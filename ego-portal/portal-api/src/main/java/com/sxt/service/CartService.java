package com.sxt.service;

import com.sxt.model.OrderVo;

import java.util.List;
import java.util.Map;

/**
 * 用户操作购物车车
 * 需要将购物车保存在redis 里面
 * @author WWF
 * @title: CartService
 * @projectName ego
 * @description: com.sxt.service
 * @date 2019/5/29 18:43
 */
public interface CartService {
    /**
     * 这个标签，是我唯一的标签
     * @param label
     * @param goodsId
     * 商品添加的数目
     * @param goodsNum
     */
    void  addCart(String label,Integer goodsId,Integer goodsNum,String uss);

    /**
     * 得到总数量
     * @param label
     * @return
     */
    long getTotal(String label,String uss);
    /**
     * 以下两个方法，用于商品的回显
     */
    /**
     * 获得商品的ids
     * @param label
     * @return
     */
    List<Integer> getGoodsList(String label,String uss);

    /**
     * 获得商品的详情
     * @param label
     * @return
     */
    Map<Integer,Integer> getGoodsInfo(String label,String uss);
    /**
     * 数据转移，从redis里面转移到数据库
     * @param label
     * @param uid
     */

    void transDataFromRedisToMysql(String label,Integer uid);

    /**
     * 清空购物车
     * @param uid
     * @param goodsIdList
     */
    void clearCart(Integer uid, List<Integer> goodsIdList);

    /**
     * 恢复购物车
     * @param orderVo
     */
    void recovery(OrderVo orderVo);
}
