package com.sxt.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.sxt.domain.Goods;
import com.sxt.domain.GoodsAttribute;
import com.sxt.domain.GoodsAttributeExample;
import com.sxt.domain.GoodsExample;
import com.sxt.mapper.GoodsAttributeMapper;
import com.sxt.service.GoodsAttributeService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


/**
 * @author WWF
 * @title: GoodsAttributeServiceImpl
 * @projectName ego
 * @description: com.sxt.service.impl
 * @date 2019/5/22 19:57
 */
@Service
public class GoodsAttributeServiceImpl extends AService<GoodsAttribute> implements GoodsAttributeService{
    @Autowired
    private GoodsAttributeMapper dao;
    @PostConstruct
    public void setDao(){// 构造器执行完了，我执行
        super.dao=this.dao;
    }

    @Override
    public Integer batchDeleteAttributeParam(String ids) {
        if (ids==null&&ids==""){
            throw new RuntimeException("id不能为空");
        }
        String[] split = ids.split(",");
        List<Integer> list=new ArrayList<Integer>();
        for (String s:split){
            list.add(Integer.valueOf(s));

        }
        //创建查询条件
        GoodsAttributeExample goodsExample = new GoodsAttributeExample();
        GoodsAttributeExample.Criteria criteria = goodsExample.createCriteria();
        criteria.andIdIn(list);
        GoodsAttribute goods = new GoodsAttribute();
        int i = dao.deleteByExample(goodsExample);
        return i;
    }
}
