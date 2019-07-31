package com.sxt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RouterController {

    /**
     * 查询订单是否完成
     */

    @RequestMapping("/order/wait")
    public String toWait() {
        return "wait";
    }
    @RequestMapping("/order/ok")
    public String ok(String orderSn,Model model) {
        model.addAttribute("orderSn", orderSn);
        return "ok";
    }
}
