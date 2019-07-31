package com.sxt;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sxt.domain.Pay;
import com.sxt.service.PayService;

public class PayApp {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        app.start();
        try {
            System.out.println("支付服务已经启动");
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
