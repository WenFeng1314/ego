package com.sxt.listener;

import com.alibaba.fastjson.JSON;
import com.sxt.domain.Admin;
import com.sxt.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 监听消息
 * @author WWF
 * @title: UserLoginListener
 * @projectName ego
 * @description: com.sxt.listener
 * @date 2019/6/1 22:39
 */
@Component
public class UserLoginListener implements MessageListener {
    @Autowired
    private CartService cartService;
    @Autowired
    private JedisPool jedisPool;
    /**
     * 监听消息
     */
    @JmsListener(destination = "wwf.user.login.topic")
    public void onMessage(Message message) {
        TextMessage textMsg=(TextMessage) message;
        try {
            String tokenLabel = textMsg.getText();// token 是用户登录的票据
            String[] tokenLabels = tokenLabel.split("#");
            Admin admin = getUserByUss(tokenLabels[0]);
            if (admin==null){
                return;
            }else {
               // 用户登录了
               //有token 就有用户
                //sso-service发了一个消息，并且portal-service 收到该消息，需要数据转移
                cartService.transDataFromRedisToMysql(tokenLabels[1],admin.getId().intValue());
            }
            message.acknowledge();

        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
    /**
     * 通过uss 来判断该用户是否登录
     * @param uss
     *  来做浏览器的cookie
     * @return
     *  用户对象，如果用户对象==null ，代表没有登录，否则，代表以及登录了
     */
    public Admin getUserByUss(String uss) {
        if(uss==null||uss.equals("")) {
            return null ;
        }
        Jedis jedis = jedisPool.getResource();
        if(!jedis.exists(uss)) {
            // 用户没有登录
            return null;
        }
        String userJson = jedis.get(uss);
        //  admin 本来位于sso 系统的domain 里面，现在我们在protal 里面需要使用，需要做类的提升 将sso-》core（common）
        return JSON.parseObject(userJson,Admin.class);
    }
}
