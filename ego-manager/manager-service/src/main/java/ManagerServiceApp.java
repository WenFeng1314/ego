import com.alibaba.fastjson.JSON;
import com.sxt.domain.Goods;
import com.sxt.model.Page;
import com.sxt.service.GoodsCategoryService;
import com.sxt.service.GoodsService;
import com.sxt.utils.TreeNode;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author WWF
 * @title: ManagerServiceApp
 * @projectName ego
 * @description: PACKAGE_NAME
 * @date 2019/5/21 21:10
 */
public class ManagerServiceApp {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        context.start();
      // GoodsService goodsService = context.getBean(GoodsService.class);
     //  goodsService.importAllStockToRedis();
     //  System.out.println("导入成功");
     //   testQuTime(goodsService);
      /*  long start=System.currentTimeMillis();
        //第一次全部加载
        testTreeNode(goodsCategoryService);
        long end = System.currentTimeMillis();
        System.out.println("第一次"+(end-start)/1000+"s");
        long start2=System.currentTimeMillis();
        //第二次加载
        testTreeNode(goodsCategoryService);
        long end2 = System.currentTimeMillis();
        System.out.println("第一次"+(end2-start2)/1000+"s");
*/
        try {
            System.out.println("提供者。。。");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public static void testTreeNode(GoodsCategoryService goodsCategoryService){
        List<TreeNode> treeNodes = goodsCategoryService.loadAllTree();
        String string = JSON.toJSONString(treeNodes);
        System.out.println(string);
    }
    public static void testQuTime(GoodsService goodsService){
        Date date = new Date(0);
        Long total = goodsService.countByTime(date, new Date());
        System.out.println(total);
        Page<Goods> goodsPage = goodsService.queryByTime(date, new Date(), 1, 10);
        List<Goods> results = goodsPage.getResults();
        for (Goods result : results) {
            System.out.println(result);

        }

    }

}
