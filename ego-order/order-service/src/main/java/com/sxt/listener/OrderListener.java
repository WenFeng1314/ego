package com.sxt.listener;

import com.sxt.model.OrderVo;
import com.sxt.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;


/**
 * @author WWF
 * @title: OrderListener
 * @projectName ego
 * @description: com.sxt.listener
 * @date 2019/6/3 18:43
 */
@Component
public class OrderListener implements MessageListener {
    @Autowired
    private OrderService orderService;

    @JmsListener(destination = "EGO-ORDER-PRE")
    public void onMessage(Message message) {
        //下单
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
            OrderVo orderVo = (OrderVo) objectMessage.getObject();
            System.out.println("订单的详情开始：");
            System.out.println("用户id:"+orderVo.getUid());
            System.out.println("购物车有几件商品："+orderVo.getGoodsVoList().size());
            System.out.println("总金额："+orderVo.getTotalMoney());
            System.out.println("订单的详情结束：");
            orderService.createPreOrder(orderVo);//调方法，生成一个预订单
            message.acknowledge();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
