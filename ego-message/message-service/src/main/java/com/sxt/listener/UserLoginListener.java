package com.sxt.listener;

import com.sxt.domain.WeichatMessage;
import com.sxt.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WWF
 * @title: UserLoginListener
 * @projectName ego
 * @description: com.sxt.listener
 * @date 2019/6/2 13:02
 */
@Component
public class UserLoginListener implements MessageListener {
    @Autowired
    private MessageService messageService;
    @JmsListener(destination = "wwf.user.login.topic")
    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;
        try {
            String tokenAndLabel = textMessage.getText();
            String token = tokenAndLabel.split("#")[0];
            // token 通过token 可以换用户,是啥意思？
            WeichatMessage weichatMessage = new WeichatMessage();
            weichatMessage.setToUser("otaol5qBtJVgyzkW-MqCwsrr4Ik0");
            weichatMessage.setTemplateId("Bh-I92GF1CXTLN9cjo-izx0x2X298sYMhOawYOu8qC8");
            weichatMessage.setUrl("https://www.sso.ego.com");
            Map<String,Map<String,String>> data=new HashMap<String, Map<String, String>>();
            data.put("user",WeichatMessage.getMap("WWF",""));
            data.put("type",WeichatMessage.getMap("pc端",""));
            data.put("username",WeichatMessage.getMap("小爱同学",""));
            data.put("time",WeichatMessage.getMap("2019-06-01",""));
            data.put("network",WeichatMessage.getMap("EGO商城",""));
            data.put("url",WeichatMessage.getMap("www.manager.com",""));
            weichatMessage.setData(data);
            messageService.sendMessage(weichatMessage);
            textMessage.acknowledge();//方法进行确认

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
