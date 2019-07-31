package com.sxt.service;

import com.sxt.domain.GoodsAttribute;

/**
 * @author WWF
 * @title: GoodsAttributeService
 * @projectName ego
 * @description: com.sxt.service
 * @date 2019/5/22 19:55
 */
public interface GoodsAttributeService extends IService<GoodsAttribute> {
    /**
     * 批量删除
     */
    Integer batchDeleteAttributeParam(String ids);
}
