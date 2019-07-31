package com.sxt.service;

import com.sxt.domain.Order;
import com.sxt.model.OrderVo;
import com.sxt.model.Page;

/**
 * @author WWF
 * @title: OrderService
 * @projectName ego
 * @description: com.sxt.service
 * @date 2019/6/3 12:39
 */
public interface OrderService{
    /**
     * 创建一个预订单
     */
    void createPreOrder();
    /**
     * 创建一个预订单
     */
    void createPreOrder(OrderVo orderVo);

    /**
     * 在设定的时间里，没有支付成功，取消订单
     * @param orderSn
     */
    void autoCancel(String orderSn);


    /**
     * 根据 用户和状态来查询订单
     * @param uid
     *  用户id
     * @param status
     * 订单的状态
     * @return
     */
    Page<Order> findOrders(Integer uid, Integer status, int page , int size);

    /**
     * 支付宝通知我们的订单支付完成，我们把订单该为已支付
     * @param orderSn
     */
    void payComplate(String orderSn);

    /**
     * 查询订单是否支付
     * @param orderSn
     * 订单编码
     * @return
     */
    int queryOrderPayStatus(String orderSn);
}
