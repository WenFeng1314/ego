package com.sxt.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.sxt.config.AlipayConfig;
import com.sxt.domain.Pay;
import com.sxt.service.PayService;

@Service
public class PayServiceImpl implements PayService{

    private static AlipayTradeService  tradeService;

    static {
        Configs.init("zfbinfo.properties");
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Override
    public String pay(Pay pay, int type) {
        switch (type) {

            case 1: // 电脑支付
                return pcPay(pay);

            case 2: // 扫描支付
                return scanPay(pay);

            default: // 别的不支付
                return null ;
        }
    }

    /**
     * 扫描支付
     * @param pay
     * @return
     */
    private String scanPay(Pay pay) {

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(pay.getSubject()).setTotalAmount(pay.getTotalAmount()).setOutTradeNo(pay.getOutTradeNo())
                .setUndiscountableAmount(pay.getUndiscountableAmount()).setSellerId(pay.getSellerId()).setBody(pay.getBody())
                .setOperatorId(pay.getOperatorId()).setStoreId(pay.getStoreId())
                .setTimeoutExpress(pay.getTimeoutExpress())
                .setNotifyUrl(pay.getNotifyUrl());//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
        // 向支付宝发起请求
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        // 解析参数
        if(result.getTradeStatus()==TradeStatus.SUCCESS) {
            return result.getResponse().getQrCode();
        }
        return null;
    }

    /**
     * 电脑支付
     * @param pay
     * @return
     */
    private String pcPay(Pay pay) {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
        String jsonString = JSON.toJSONString(pay);
        System.out.println(jsonString);
        alipayRequest.setBizContent(jsonString);
        String result = null ;
        try {
            // 发起请求
            result = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return result;
    }

}
