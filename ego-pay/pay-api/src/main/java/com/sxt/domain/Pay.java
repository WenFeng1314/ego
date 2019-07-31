package com.sxt.domain;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 支付参数的类
 * @author CodeLab
 *
 */
public class Pay implements Serializable{

    private static final long serialVersionUID = 1L;
    /**
     * 订单号
     */
    @JSONField(name="out_trade_no")
    private String outTradeNo;
    /**
     * 订单的标题
     */
    private String subject;
    /**
     * 总金额
     */
    @JSONField(name="total_amount")
    private String totalAmount;
    /**
     * 折扣金额
     */
    @JSONField(name="undiscountable_amount")
    private String undiscountableAmount;
    /**
     * 买家id ，也是pid
     */
    @JSONField(name="seller_id")
    private String sellerId;
    /**
     * 订单的描述
     */
    private String body;
    /**
     * 操作员id （写死）
     * 在真实的环境里面，需要申请才有
     */
    @JSONField(name="operator_id")
    private String operatorId = "test_operator_id";
    /**
     * 门店id
     * 就是你开店需要申请
     */
    @JSONField(name="store_id")
    private String storeId =  "test_store_id";
    /**
     * 订单的超时时间
     */
    @JSONField(name="timeout_express")
    private String  timeoutExpress;

    /**
     * 支付成功的通知地址（理解它，需要和returnUrl放在一起理解）
     */
    @JSONField(name="notify_url")
    private String notifyUrl;

    @JSONField(name="product_code")
    private String productCode = "FAST_INSTANT_TRADE_PAY";

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUndiscountableAmount() {
        return undiscountableAmount;
    }

    public void setUndiscountableAmount(String undiscountableAmount) {
        this.undiscountableAmount = undiscountableAmount;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getTimeoutExpress() {
        return timeoutExpress;
    }

    public void setTimeoutExpress(String timeoutExpress) {
        this.timeoutExpress = timeoutExpress;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public static void main(String[] args) {
        Pay pay = new Pay();
        pay.setOutTradeNo("201906041004");
        pay.setSubject("在ego商城消费了");
        pay.setTotalAmount("12000");
        pay.setBody("买了一个女朋友");
        String jsonString = JSON.toJSONString(pay);
        System.out.println(jsonString);
    }

}
