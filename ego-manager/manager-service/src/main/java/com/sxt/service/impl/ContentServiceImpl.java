package com.sxt.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.sxt.domain.Content;
import com.sxt.domain.ContentExample;
import com.sxt.mapper.ContentMapper;
import com.sxt.model.Page;
import com.sxt.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 该方法可以给父类里面注入一个子类的dao
 * @author WWF
 * @title: ContentServiceImpl
 * @projectName ego
 * @description: com.sxt.service.impl
 * @date 2019/5/21 20:34
 */
@Service
public class ContentServiceImpl extends AService<Content> implements ContentService {

    @Autowired
    private ContentMapper dao;

    /**
     *
     */
    @PostConstruct
    public void setDao() {
        super.dao = this.dao;
    }

    /**
     *
     * @param catId
     * 当前页
     * @param page
     *  条数
     * @param size
     * 分页对象
     * @return
     */
    @Override
    public Page<Content> findByPage(Integer catId, Integer page, Integer size) {
//	 select * from t_content where catId =  ？ limit ，
        ContentExample contentExample = new ContentExample();
        contentExample.createCriteria().andCategoryIdEqualTo(catId.longValue());
        //分页拦截 sql语句
        com.github.pagehelper.Page<Object> startPage = PageHelper.startPage(page, size);
       //根据条件查询
        List<Content> contents = dao.selectByExample(contentExample);
        return new Page<Content>(page,size,startPage.getTotal(),contents);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @Override
    public Integer batchDeleteContent(String ids) {
        if (ids==null&&ids==""){
            throw new RuntimeException("id不能为空");
        }
        //1.分割ids
        String[] split = ids.split(",");
        //2.循环split,创建一个集合去装载所有的content的id
        List<Long> list=new ArrayList<Long>();
        for (String s : split) {
            list.add(Long.parseLong(s));
        }
        //3.创建删除条件
        ContentExample contentExample = new ContentExample();
        ContentExample.Criteria criteria = contentExample.createCriteria();
        criteria.andIdIn(list);
        //4.删除
        int result = dao.deleteByExample(contentExample);
        return result;
    }

    /**
     * 获得广告数据
     * @return
     */
    @Override
    public List<Content> getAds() {
        ContentExample contentExample = new ContentExample();
        contentExample.createCriteria().andCategoryIdEqualTo(Long.valueOf("89"));
        List<Content> contents = dao.selectByExample(contentExample);
        return contents;
    }
}
