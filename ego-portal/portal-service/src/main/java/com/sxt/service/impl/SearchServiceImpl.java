package com.sxt.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sxt.service.SearchService;
import com.sxt.vo.GoodsVo;
import com.sxt.vo.SearchResult;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 搜索的实现类型
 * @author WWF
 * @title: SearchServiceImpl
 * @projectName ego
 * @description: com.sxt.service.impl
 * @date 2019/5/27 19:59
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private HttpSolrServer solrServer;
    /**
     * 搜索的实现
     * 使用solr 来搜索
     */

    @Override
    public SearchResult doSearch(Boolean isCat,String keywords,Map<String,String> sorts,String scope,Map<String,Boolean> fq, Integer currentPage, Integer pageSize) {

        //		solr solr 的一系列操作
        //	 但是我们必须知道，solr 要搜索，先要导入，不导入，搜索不了
        String q="keywords:"+keywords;
        SolrQuery query = new SolrQuery(q);
        //分页查询
        query.setStart((currentPage-1)*pageSize);
        query.setRows(pageSize);
        //排序查询，我们排序查询只有一个，但是solr支持多个排序
       sorts.forEach((k,v)->{
           query.addSort(k,v.equals("ASC")? SolrQuery.ORDER.asc: SolrQuery.ORDER.asc);
       });
       //区间查询
        List<String> fqList=new ArrayList<String>();
        if (scope!=null&&scope.contains("-")){
            String[] split = scope.split("-");
           fqList.add("goods_price:[" + split[0] + " TO " + split[1] + "]");
        }
        fq.forEach((k,v)->{
            fqList.add(k+":"+v);
        });

        String[] fqArrays=null;
        if (isCat){// 需要分类查询，我们使用过滤来保持精确
            fqArrays = new String[fqList.size()+1];
            fqArrays[fqList.size()]= "goods_cat_id:"+keywords ; //当前的keyword 是使用分类id 来查询的，我们使用过滤查询来保持精度
            for (int i = 0; i < fqArrays.length-1; i++) {
                fqArrays[i]=fqList.get(i);

            }
        }else {
            fqArrays = new String[fqList.size()];
            for (int i = 0; i < fqArrays.length; i++) {
                fqArrays[i]=fqList.get(i);

            }
        }



        query.setFilterQueries(fqArrays);

        //高亮查询
        query.setHighlight(true);
        query.addHighlightField("goods_name");
        query.setHighlightSimplePre("<font color='red'");
        query.setHighlightSimplePost("</font>");
        ArrayList<GoodsVo> goodsVoList = new ArrayList<>();
        long total=0L;
        try {
            //使用solr搜索
            QueryResponse queryResponse = solrServer.query(query);
            //solr搜索的结果
            SolrDocumentList results = queryResponse.getResults();
             total = results.getNumFound();// 获取总条数
            //			搜索的结果里面，没有高亮字段，需要手动处理
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            for (SolrDocument solrDocument : results) {
                GoodsVo goodsVo=solrDoc2GoodsVo(solrDocument);
                if(highlighting.get(goodsVo.getId().toString())!=null&&highlighting.get(goodsVo.getId().toString()).size()>0){
                    String string = highlighting.get(goodsVo.getId().toString()).get("goods_name").get(0);
                    System.out.println("高亮的值为："+string);
                    goodsVo.setGoodsNameHl(string);//高亮的值
                }

                goodsVoList.add(goodsVo);

            }
            //	:String:id Map.String 字段 （goods_name） Map.list(高亮的值)
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        SearchResult searchResult = new SearchResult();
        searchResult.setResult(goodsVoList);
        searchResult.setCurrentPage(currentPage);
        searchResult.setTotal(total);

        return searchResult;
    }
    /**
     * 将solr 搜索出来的文档类型转换为goodsVo 对象
     * @param solrDocument
     * @return
     */
    private GoodsVo solrDoc2GoodsVo(SolrDocument solrDocument) {
        GoodsVo goodsVo = new GoodsVo();
        goodsVo.setId(Integer.valueOf(solrDocument.getFieldValue("id").toString()));
        goodsVo.setOriginImg(solrDocument.getFieldValue("goods_img").toString());
        goodsVo.setShopPrice(Double.valueOf(solrDocument.getFieldValue("goods_price").toString()));
        goodsVo.setGoodsNameHl(solrDocument.getFieldValue("goods_name").toString());
        return  goodsVo;

    }
}
