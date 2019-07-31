package com.sxt.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.sxt.domain.Admin;
import com.sxt.domain.AdminExample;
import com.sxt.mapper.AdminMapper;
import com.sxt.model.PwdError;
import com.sxt.model.UserNotFonud;
import com.sxt.service.LoginService;
import com.sxt.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author WWF
 * @title: LoginServiceImpl
 * @projectName ego
 * @description: com.sxt.service.impl
 * @date 2019/5/30 8:48
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Override
    public String login(String username, String password, Map<String,Object> ext) {
        //登录的方法
        AdminExample adminExample = new AdminExample();
        adminExample.createCriteria().andUserNameEqualTo(username);
        List<Admin> users = adminMapper.selectByExample(adminExample);

        if (users==null||users.size()==0){
            //我们可以让用户去注册
            throw new UserNotFonud("用户"+username,701);
        }
        // 用户名应该是唯一的username ，可能是电话号码，email ，
        if (users.size()>1){
            throw new RuntimeException("数据库里面存在相同的用户名称，数据库错误");
        }
        // 要登录的用户
        // 比对密码,数据库的密码是已经加密后的密码 MD5
        Admin admin = users.get(0);
        String ecSalt = admin.getEcSalt();
        String md5Pwd = Md5Util.GetMD5WithSalt(password, ecSalt);
        String dbPwd = admin.getPassword();
        if (!md5Pwd.equals(dbPwd)){
            // 用户的密码错误
            throw new PwdError(703);
        }
        // 在存入redis 之前，给密码打码
        admin.setPassword("*****");
        admin.setEcSalt("*****");
        Jedis jedis=null;
        String token=null;
        try {
            jedis = jedisPool.getResource();
            token = UUID.randomUUID().toString();
            // 将登录的用户存入到redis 里面了
            jedis.set(token, JSON.toJSONString(admin));
            //设置存储时间
           jedis.expire(token,7*24*60*60);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        // 发送消息
        String msg=null;
        try {
            if (ext!=null&&ext.containsKey("EGO-USER-LABEL")){
                String label = ext.get("EGO-USER-LABEL").toString();
                msg=token+"#"+label;

            }
            jmsTemplate.convertAndSend("wwf.user.login.topic",msg);
            System.out.println("主题里面的消息也发送成功了");
        } catch (JmsException e) {
            e.printStackTrace();
        }


        return token;
    }
}
