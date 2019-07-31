package com.sxt.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016092000552404";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCjCeadY3f/xV59wBrwGeAA8ZMFoJtnG6TGV0yIU5PGQNqHydoIk9ni7wIQuPCZBoPqIpvTOmnjxDIMAOxxDMBrr1XEXGjY8mSyVka5frNl4zF/tUlwp3v8WLdodnKRXzdCZj7HunudZe6JOvNm11Sh2tbp/p+gFoLGOr2MLybEvqvw1C+BGT2NqNlBfKVE5S7cadF9BY0wkhxtpuU0xwwMXNt4KqB6Q4yewMdGkE+B538u9vfR3jY7Znw6HhJg37ysZloJieRuBQSR5qDZ/p17b1fwOj6uz9Eowft5uslHyH5VBxK64Kp57sYxKHbzFRucrbDpET6DwbaqPmpcogy1AgMBAAECggEATNLggO8a30GAKd74koSOwcMTFQa901L6TJ9cJ6zCKBvtHSjm/T214LHglkKe1Gx2gj/2jwyqUrHjJzbUMtzgWp2O22zz1q449rOitctW+z6X12Z0CYpR6PFg0u9lQszL/BSzpmdf0iXyxWUqxh3W+kFlHXNmgge5bXDGWiTprrCr+J8fg0Tv0Uh6TXa9+uxuEJGRK4H7jSP4zCrBcFgbeBDNeK17SVXdMduwlUs+ahC/oA0q+A27IWpt1s/tDZlhKb4LWJ6w1bUJjF3kmTOLqdXWN33VXb5IzqEdm3QVkdgCYEuV526Uzq3oZhIzP93g+3NfOib+l7d8uskmzHPJwQKBgQDloXl/64q8UtOL1YvObwSqjrnLGvfUKF2btc1+h28Q757P7Wwt1Ntxb9JPf0ydLtWD/49vW6JlAVweSqOWqmb9BMV2LbSXmXpZMH53N7xvqozM6s9jkhPgEmqKisdv44Pbry0p9Etf8aXSUMPn5cuS+ilxrFf2xc+/dj3vD2HOcQKBgQC1wsxlqxcOGzivXf0QJ43KyLqpvBBd5FrzqiIXD2qzdhrnaSD7UrBdX0EUzBlQFBcytmYOqj0ViL+Szm256m3Zc7DMMaIbNkOQokvk98i5lq1wBs0ol9EmqECkWdFtQT0QmjZn+9/0x5Sjf3qj8R38+wy1oybZjAGYvvMdDqyMhQKBgQCaHqB/fMVm1Ghrh+4HpXeKSbTlXrpLSLRbF9BORfQDd/AtOfrLX/vQzO5osNXpAVAJAfNxGB2MkTcUlZ/qNYBd/V/Si6aTwUzeo6+dsGjmwZ8ywb5j4903HOUpCzAEJr2PKqmzaOVWBcf3uoFiGVdi1EgQpU18sRNYO7Ihyz8n4QKBgHeUwn79Ef0A4clWj8DKFRpd95jmFup+62u1+hmr1epeAJNQ8hcHLusvWRUCtoCdxylhIUHytJ9vSYKfd0dCuaS/3Yhp+xnmGIuWi1IHxzJ6tH6oqt9TCpJhTa4TPZqgujkrDDJbXtufVuY+X4RuhOlEjfcOcOfdmctRjYcHeXQ1AoGBAJZB6SbXMNuXiq66xoXdLlto88H5V/XO6YJdFXNgh/1l4wUXlCC67FguohqxDl4ANNo98UJKjwgcZMMGmAykUV77f7R7afOcFI8NOi42Ja56Exophdrk9IiESF02lqePv8/mBcA3VdCv/T1u65mptC3cQ+Wc3kmQ0mu6NN403HM2";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2nyeLlG7GyizB94gj8oHVSYB/6l8ZRZ9rCtxhIpn1IGbmVHSNoTpMFONyZTt5/aKTwzocTfk4MyzfWuCzYV0gdPJPEllNwELIo7G/e7KyJjXMSogqYIszQrpRFs7NlHlb7i0SckC6sLCLK0X275IMf+Px/xi8qW8fSBB4XEJNqi3cq4EvqRVidb/DDmrm30svHCQk3h/7X19FMBRCi8Rh43e/C8ZM/yOblT4MHdgFMCkbfFpSc0zOvd5f/9BaDeDqGrcgkfQAwkE879Zzcgi/szAd/B+Vu+GoWiM3o+gVpsdcllFE5PpK9ZmwvdeLTzSRty1voNCq7yGFLg2ykUJBQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8083/order/zfb/notify";
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8083/order/pay";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关 修改为测试环境的网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

