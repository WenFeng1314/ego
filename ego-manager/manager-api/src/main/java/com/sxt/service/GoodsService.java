package com.sxt.service;

import com.sxt.domain.Goods;
import com.sxt.model.OrderVo;
import com.sxt.model.Page;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author WWF
 * @title: GoodsService
 * @projectName ego
 * @description: com.sxt.service
 * @date 2019/5/21 20:27
 */
public interface GoodsService extends IService<Goods> {
    /**
     * 导入库存
     */
     void importAllStockToRedis();
    /**
     * 批量下架
     * @param ids
     */
     Integer batchDown(String ids);

    /**
     * 批量上架
     * @param ids
     * @return
     */
     Integer batchIsOnSale(String ids);
    /**
     * 批量删除
     */
    Integer batchDeleteGoods(String ids);
    /**
     * 根据商品的更新时间来查询商品
     * @param startTime
     *  起始时间
     * @param deadTime
     * 截止时间
     * @param page
     * 当前页
     * @param size
     * 显示的条数
     * @return
     */
    Page<Goods> queryByTime(Date startTime,Date deadTime,Integer page,Integer size);
    /**
     * 统计在一定时间内的商品数量
     * @param startTime
     * @param deadTime
     * @return
     */
    Long countByTime(Date startTime,Date deadTime);
    /**
     * 根据商品的ids 来批量查询商品
     * @param goodsList
     * 商品的ids
     * @return
     * 商品集合
     */
    List<Goods> findByIds(List<Integer> goodsList);

    /**
     * 执行库存--
     * @param goodsId
     * @param num
     */
    void decrStock(Integer goodsId, Integer num);

    /**
     * 执行库存回滚
     * @param goodsInfo
     */
    void recoveryStock(HashMap<Integer, Integer> goodsInfo);

    /**
     * 一次性回滚
     * @param orderVo
     */
    void recoveryStock(OrderVo orderVo);
}
