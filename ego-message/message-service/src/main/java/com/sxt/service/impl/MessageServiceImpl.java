package com.sxt.service.impl;

import com.sxt.domain.AccessToken;
import com.sxt.domain.WeichatMessage;
import com.sxt.domain.WeichatResult;
import com.sxt.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author WWF
 * @title: MessageServiceImpl
 * @projectName ego
 * @description: com.sxt.service.impl
 * @date 2019/6/2 13:48
 */
@Service// 不需要远程调用，所有的调用，使用mq 的机制完成
public class MessageServiceImpl implements MessageService {
    @Autowired// 使用spring来创建对象
    private RestTemplate restTemplate;
    private String accessToken;
    @Value("${weichat.app.id}")
    private String appId;
    @Value("${weichat.secret}")
    private String secret;
    private static final int RETRYNUM=3;
    private static String ACCESS_TOKEN_URL="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    private static String MESSAGE_URL="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";


    @Override
    @Scheduled(initialDelay = 10,fixedRate = 7100*1000)// 2 个小时刷新一次
    public void refreshToken() {
        ACCESS_TOKEN_URL=ACCESS_TOKEN_URL.replaceAll("APPID",appId).replaceAll("APPSECRET",secret);
        AccessToken token = restTemplate.getForObject(ACCESS_TOKEN_URL, AccessToken.class);
         accessToken = token.getAccessToken();
        System.out.println("token 获取成功"+accessToken);

    }

    @Override
    public void sendMessage(WeichatMessage weichatMessage) {
        Integer i = 1;
        while (i<RETRYNUM){
            boolean ok = sendWeichartMessage(weichatMessage);
            if (ok){
                //发送成功
                System.out.println("发送成功");
                return;
            }else {
                i++;
            }
        }


    }
    public boolean sendWeichartMessage(WeichatMessage weichatMessage){
        MESSAGE_URL=MESSAGE_URL.replaceAll("ACCESS_TOKEN",accessToken);
        // 将message - > json
        WeichatResult result = restTemplate.postForObject(MESSAGE_URL, weichatMessage, WeichatResult.class);
        if (result.getErrCode()==0){
            return true;
        }else {
            return false;
        }

    }
}
