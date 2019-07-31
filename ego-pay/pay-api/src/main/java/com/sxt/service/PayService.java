package com.sxt.service;

import com.sxt.domain.Pay;

/**
 * @author WWF
 * @title: PayService
 * @projectName ego
 * @description: com.sxt.service
 * @date 2019/6/5 19:41
 */
public interface PayService {

    /**
     * 调用支付
     * @param pay
     * 支付对象 （支付需要的参数）
     * @param
     * type： 支付方式
     *   1 ：电脑支付
     *   2：扫描支付
     * @return
     *  电脑支付：返回一个form 变大
     * 扫描支付：返回一个二维码的内容
     *
     */
    String pay(Pay pay, int type);
}
