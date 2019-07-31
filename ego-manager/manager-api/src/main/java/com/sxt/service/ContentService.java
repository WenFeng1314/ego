package com.sxt.service;

import com.sxt.domain.Content;
import com.sxt.model.Page;

import java.util.List;

/**
 * @author WWF
 * @title: ContentService
 * @projectName ego
 * @description: com.sxt.service
 * @date 2019/5/26 17:47
 */
public interface ContentService extends IService<Content> {
    /**
     * 通过分类id 来分页查询它里面的数据
     * 分类id
     * @param catId
     * 当前页
     * @param page
     *  条数
     * @param size
     * 分页对象
     * @return
     */
    Page<Content> findByPage(Integer catId, Integer page,Integer size);

    /**
     * 批量删除
     */
    Integer batchDeleteContent(String ids);

    /**
     * 获得广告数据
     * @return
     */
    List<Content> getAds();

}
