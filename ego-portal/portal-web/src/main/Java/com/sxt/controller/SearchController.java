package com.sxt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxt.service.SearchService;
import com.sxt.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * @author WWF
 * @title: SearchController
 * @projectName ego
 * @description: com.sxt.controller
 * @date 2019/5/30 16:59
 */
@Controller
public class SearchController {
    @Reference
    private SearchService searchService;

    /**
     *不是配置编码器 encoding
     * 在web.xml 配置的编码器，只能对post 请求编码，因为编码器值编码form的表单数据，
     * 而位于url 里面的数据，无法编码
     //	 * search/goSearch?keywords=手机&x=22&y=23
     *  get 请求的乱码问题
     *   1 重新再后端编码（以后在这里处理）
     *   2 修改tomcat的server.xml 配置文件
     * @param keywords
     * @param model
     * @return
     */

    @RequestMapping("/search/goSearch")
    public String goSearch(String keywords, Model model, String isCat, String desc){
        //在浏览器里面输入，默认为ISO-8859-1 我们需要把它转换为utf-8
        String encoding= null;
        String encodingDesc=null;
        try {
            encoding = new String(keywords.getBytes("ISO-8859-1"),"UTF-8");
            if (desc!=null){
                encodingDesc = new String(desc.getBytes("ISO-8859-1"),"UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (isCat!=null&&isCat.equals("true")){
            model.addAttribute("keywords",encodingDesc);
            model.addAttribute("catId",encoding);


        }else {
            model.addAttribute("keywords",encoding);
        }
        model.addAttribute("isCat",isCat);
        return "search";

    }
    @RequestMapping("/search/doSearch")
    @ResponseBody
    public SearchResult doSearch(
            @RequestParam(required = true) String keywords,
            @RequestParam(defaultValue = "")String sort,
            @RequestParam(defaultValue = "false") String isCat,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String scope,boolean isNew,boolean isOnSale){
        // 过滤查询的集合
        HashMap<String, Boolean> fq = new HashMap<>();
        if (isNew)fq.put("is_new",true);
        if (isOnSale)fq.put("is_onsale",true);

        // 排序查询
        HashMap<String, String> sortMap = new HashMap<>();
        if (!sort.equals("")){
            if (sort.equals("price")){
                sortMap.put("goods_price","ASC");
            }
            if (sort.equals("commentNunm")){
                sortMap.put("goods_comment_num","DESC");
            }
        }

        SearchResult search = searchService.doSearch(isCat.equals("false") ? false : true, keywords, sortMap, scope, fq, currentPage, pageSize);

        return  search;

    }
}
