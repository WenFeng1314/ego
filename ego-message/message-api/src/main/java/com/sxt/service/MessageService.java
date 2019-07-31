package com.sxt.service;

import com.sxt.domain.WeichatMessage;

/**
 * @author WWF
 * @title: MessageService
 * @projectName ego
 * @description: com.sxt.service
 * @date 2019/6/2 12:41
 */
public interface MessageService {
    /**
     *token是接口调用的唯一凭证，2h,过期，需要使用定时任务来刷新
     */
    void refreshToken();

    void sendMessage(WeichatMessage weichatMessage);
}
