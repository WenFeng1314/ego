package com.sxt.service;

import java.util.Map;

/**
 * @author WWF
 * @title: LoginService
 * @projectName ego
 * @description: com.sxt.service
 * @date 2019/5/30 8:47
 */
public interface LoginService {
    /**
     * 用户名
     * @param username
     * 密码
     * @param password
     * 登录成功后，给用户一个凭证
     */
    String login(String username, String password, Map<String,Object> ext);
}
