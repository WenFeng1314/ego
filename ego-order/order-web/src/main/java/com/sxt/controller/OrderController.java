package com.sxt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sxt.domain.Admin;
import com.sxt.domain.Order;
import com.sxt.model.OrderGoodsVo;
import com.sxt.model.OrderVo;
import com.sxt.model.Page;
import com.sxt.service.OrderService;
import com.sxt.utils.ThreadLocalUtil;
import com.sxt.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author WWF
 * @title: OrderController
 * @projectName ego
 * @description: com.sxt.controller
 * @date 2019/6/3 12:45
 */

@RestController
@RequestMapping("/order")
public class OrderController {
       @Reference
       private OrderService orderService;
       @Autowired
       private JedisPool jedisPool;

    @Autowired
    private JmsTemplate jmsTemplate;
    @GetMapping("/test")
    public String createPreOrder(){
        System.out.println("开始远程调用");
        long start=System.currentTimeMillis();
        jmsTemplate.convertAndSend("ego-order-pre","订单");
       // orderService.createPreOrder();
        long end=System.currentTimeMillis();
        System.out.println("远程调用结束，耗时："+(end-start)/1000+"s");
        return  "ok";

    }
    /**
     * 通过状态来查询订单
     * @param status
     * @return
     */
    @GetMapping("/getOrders")
    public Result getOrders(@RequestParam(required=true)Integer status,@RequestParam(defaultValue="1")Integer page ,@RequestParam(defaultValue="10")Integer size) {
        Admin user = (Admin) ThreadLocalUtil.get("user");
        Page<Order> orders = orderService.findOrders(user.getId().intValue(),status,page,size);

        return Result.ok(orders);
    }
    @GetMapping("/submit")
    public String submitCart(@RequestParam (required = true) String callBackFunc,
                             HttpServletRequest request,
                             OrderVo orderVo){
        String jsonpCallback = request.getParameter("callBackFunc");

        List<OrderGoodsVo> goodsVoList = orderVo.getGoodsVoList();
        Double totalMoney=0.0;
        for (OrderGoodsVo orderGoodsVo : goodsVoList) {

            totalMoney+=orderGoodsVo.getShopPrice()*orderGoodsVo.getNum();
        }
        orderVo.setTotalMoney(totalMoney);
        //在登录成功之后，把用户放入线程池里，
        //而tomcat线程池，会自动回收
        Admin user = (Admin)ThreadLocalUtil.get("user");
        orderVo.setUid(user.getId().intValue());//同一个线程
        //把对象发送过去,而此对象需要序列化，jdk的序列化是递归的形式，所以，
        //此对象里面的属性，含有对象的也需要序列化
        jmsTemplate.convertAndSend("EGO-ORDER-PRE",orderVo);
        String result = JSON.toJSONString(Result.ok());
        return callBackFunc+"("+result+")";

    }
    /**
     * 当支付宝把钱打给我们时，它会使用该url 通知我们的系统
     * @param orderSn
     * @return
     */
    @GetMapping("/zfb/notify")
    public Result afbNofity(String orderSn) {
        orderService.payComplate(orderSn);
        return Result.ok();
    }

    @GetMapping("/query")
    public Result queryOrderPayStatus(String orderSn){
          //根据订单编号去查询数据库，该订单的状态
        int reuslt = orderService.queryOrderPayStatus(orderSn);

        if(reuslt==1) {
            return Result.ok(orderSn);
        }else {
            return Result.error();
        }
    }
}
