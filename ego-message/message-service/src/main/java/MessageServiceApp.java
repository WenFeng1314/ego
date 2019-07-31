import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author WWF
 * @title: MessageServiceApp
 * @projectName ego
 * @description: PACKAGE_NAME
 * @date 2019/6/2 14:17
 */
public class MessageServiceApp {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        app.start();
        System.out.println("message已经准备就绪了");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
