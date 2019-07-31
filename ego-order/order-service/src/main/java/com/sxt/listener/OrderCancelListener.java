package com.sxt.listener;

import com.sxt.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author WWF
 * @title: OrderCancelListener
 * @projectName ego
 * @description: com.sxt.listener
 * @date 2019/6/4 20:03
 */
@Component
public class OrderCancelListener implements MessageListener {
    @Autowired
    private OrderService orderService;



    @JmsListener(destination = "ORDER.DELETE.QUEUE")
    public void onMessage(Message message) {
        //得到文本消息
        TextMessage textMessage=(TextMessage)message;
        try {
            String orderSn = textMessage.getText();
            orderService.autoCancel(orderSn);
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
