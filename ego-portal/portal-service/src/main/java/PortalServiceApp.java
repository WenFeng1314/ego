import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author WWF
 * @title: PortalServiceApp
 * @projectName ego
 * @description: PACKAGE_NAME
 * @date 2019/5/27 11:27
 */
public class PortalServiceApp {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        context.start();
        //ImportServiceImpl importServiceImpl = (ImportServiceImpl)context.getBean("ImportServiceImpl");
        // CartService bean = context.getBean(CartService.class);
       //  CartServiceImpl1.testDataTrans(bean);
        try {
            System.out.println("既是消费者，也是提供者。。。");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
