package com.sxt.service;

import com.sxt.vo.SearchResult;

import java.util.Map;

/**
 * @author WWF
 * @title: SearchService
 * @projectName ego
 * @description: com.sxt.service
 * @date 2019/5/27 17:41
 */
public interface SearchService {
    /***
     * 实现搜索
     * @param keywords
     *  关键在
     * @param currentPage
     * 当前页
     * @param pageSize
     * 每页显示的条数
     * @return
     * @param sorts 排序字段
     * k String： 你要使用那个字段排序
     * v String  asc decs
     *
     * @param
     * scope 价格区间  100-2000
     * @param fq
     * String: 那个字段
     * Boolean：条件的值
     * SearchResult
     */
    SearchResult doSearch(Boolean isCat,String keywords, Map<String,String> sorts,String scope,Map<String,Boolean> fq,Integer currentPage, Integer pageSize);
}
