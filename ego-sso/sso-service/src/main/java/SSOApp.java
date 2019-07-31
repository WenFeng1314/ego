import com.sxt.service.LoginService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @author WWF
 * @title: SSOApp
 * @projectName ego
 * @description: PACKAGE_NAME
 * @date 2019/5/29 17:08
 */
public class SSOApp {
    public static void main(String[] args) {

        ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        app.start();
        System.out.println("SSO提供者启动成功...");
        // 测试登录
     //   LoginService loginService = app.getBean(LoginService.class);
      //  String whsxt = loginService.login("whsxt", "123456");
       // System.out.println("产生的key："+whsxt);

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
