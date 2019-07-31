package com.sxt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxt.model.PwdError;
import com.sxt.model.UserNotFonud;
import com.sxt.service.LoginService;
import com.sxt.utils.CookieUtil;
import com.sxt.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @author WWF
 * @title: LoginController
 * @projectName ego
 * @description: com.sxt.controller
 * @date 2019/5/30 10:59
 */
@Controller
public class LoginController {
    @Reference
    private LoginService loginService;
    @GetMapping("/login")
    public String login(String currentUrl, Model model){//去登录页面
        // 将跳过来的url 保存在页面里面，等我登录成功，我再跳回去
        model.addAttribute("redirectUrl",currentUrl);
        return "login";

    }
    @PostMapping("/user/sign")
    @ResponseBody
    public Result doLogin(
            @RequestParam(required = true) String username,
            @RequestParam(required = true) String password,
            HttpServletRequest request){

        String token=null;
    try {
       //登录的异常
        String label = CookieUtil.getCookieValue(request, "EGO-USER-LABEL");
        HashMap<String,Object> ext=new HashMap<String, Object>();
        if (label!=null){
            ext.put("EGO-USER-LABEL",label);
        }
        token = loginService.login(username, password,ext);
    }catch (Exception e){
        if (e instanceof UserNotFonud){// 登录时用户不存
            return Result.error(701,"该用户不存在");

        }if (e instanceof PwdError){
            return Result.error(703,"该用户密码错误");
        }
        if (token==null&&token.equals("")){
            return Result.error(701,"发送了未知的错误");
        }
    }
    saveTokenToCookie(token);
    return Result.ok();

    }
    /**
     * 将token 保存在cookie 里面， 而且该cookie 必须在一级域名里面才行
     * @param token
     */
    private void saveTokenToCookie(String token) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        CookieUtil.setCookie(attributes.getRequest(),attributes.getResponse(),"EGO-USS",token,7*24*60*60);
    }

}
