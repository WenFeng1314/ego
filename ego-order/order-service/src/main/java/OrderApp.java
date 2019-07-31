import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author WWF
 * @title: OrderApp
 * @projectName ego
 * @description: com.sxt.service.impl
 * @date 2019/6/3 13:57
 */
public class OrderApp {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        app.start();
        try {
            System.out.println("OrderApp已启动...");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
