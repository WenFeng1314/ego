package com.sxt.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxt.domain.Goods;
import com.sxt.model.Page;
import com.sxt.service.GoodsService;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 增量的导入
 *
 * @author WWF
 * @title: ImportServiceImpl
 * @projectName ego
 * @description: com.sxt.service.impl
 * @date 2019/5/27 18:31
 */
@Component("ImportServiceImpl")
public class ImportServiceImpl {
    //通过远程调用来查询商品
    @Reference
    private GoodsService goodsService;

    @Autowired
    private HttpSolrServer solrServer;

    @Value("${page.size}")
    private Integer pageSize;

    private Date startTime = new Date(0);

    /**
     * 15 min 导入一次
     * 需要增量的导入
     */
   // @Scheduled(initialDelay = 10, fixedRate = 20 *1000)
    public void importSolrFromDb() {
        System.out.println("开始执行定时任务了");
        /**
         * 导入时的性能问题 200提交一次
         * 安全性问题
         */
        Date deadTime = new Date();
        Long total = goodsService.countByTime(startTime, deadTime);
        Long totalPage = total % pageSize == 0 ? total / pageSize : (total / pageSize + 1);
        for (int page = 1; page <=totalPage; page++) {
            //   Page<Goods> goodsPage = goodsService.findByPage(1, Integer.MAX_VALUE);
            Page<Goods> goodsPage = goodsService.queryByTime(startTime, deadTime, page, pageSize);
            List<SolrInputDocument> goodsDoc = page2Doc(goodsPage);
            try {
                solrServer.add(goodsDoc);
                solrServer.commit();
               System.out.println("第" + page + "页导入完成");
            } catch (SolrServerException e) {
                System.out.println("第" + page + "页导入失败");
                e.printStackTrace();

            } catch (IOException e) {
                System.out.println("第" + page + "页导入失败");
                e.printStackTrace();

            }


        }
        startTime = deadTime;// 更新开始时间



    }

    private List<SolrInputDocument> page2Doc(Page<Goods> goodsPage) {
        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        List<Goods> results = goodsPage.getResults();
        for (Goods goods : results) {
            docs.add(goods2Doc(goods));

        }
        return docs;
    }

    /**
     * 将商品转为为goodsDoc 对象
     *
     * @param goods
     * @return
     */

    private SolrInputDocument goods2Doc(Goods goods) {
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        // 以下字段必须在solr 里面存在才行
        solrInputDocument.addField("id", goods.getId());
        solrInputDocument.addField("goods_name", goods.getGoodsName());
        solrInputDocument.addField("goods_price", goods.getShopPrice().floatValue());
        solrInputDocument.addField("goods_img", goods.getOriginalImg());
        solrInputDocument.addField("goods_comment_num",
                goods.getCommentCount() == null ? 0 : goods.getCommentCount().intValue());
       solrInputDocument.addField("is_new",goods.getIsNew()==true?true:false);
       solrInputDocument.addField("is_onsale",goods.getIsOnSale()==true?true:false);
       solrInputDocument.addField("goods_cat_id",goods.getCatId());
        return solrInputDocument;


    }
}
