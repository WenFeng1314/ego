package com.sxt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxt.domain.Pay;
import com.sxt.service.PayService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    /**
     * 去支付
     * @param orderSn
     * @param orderName
     * @param totalAmount
     * @param type
     * @return
     */
    @PostMapping("/toPay")
    public String toPay(@RequestParam(required=true)String orderSn,
                        @RequestParam(required=true)String orderName,
                        @RequestParam(required=true)String totalAmount,
                        @RequestParam(defaultValue="2")Integer type, Model model,
                        HttpServletResponse response) {
        Pay pay = new Pay();
        pay.setOutTradeNo(orderSn);
        pay.setSubject(orderName);
        pay.setTotalAmount(totalAmount);
        String result = payService.pay(pay, type);

        if (type == 2) {
            model.addAttribute("qrCode", result);
            model.addAttribute("orderSn", orderSn);
            model.addAttribute("orderName", orderName);
            return "pay";
        }
        if (type == 1) {
            PrintWriter writer = null;
            String htmlPre = "<!DOCTYPE html>\r\n" +
                    "<html lang=\"en\">\r\n" +
                    "<head>\r\n" +
                    "  <meta charset=\"UTF-8\">\r\n" +
                    "</head>\r\n" +
                    "<body>";
            String htmlSuffix = "</body>\r\n" +
                    "</html>";
            try {
                String payHtml = htmlPre + result + htmlSuffix;
                System.out.println(payHtml);
                response.setCharacterEncoding("UTF-8");
                writer = response.getWriter();
                writer.write(payHtml);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }

        }

        return null;

    }
}
