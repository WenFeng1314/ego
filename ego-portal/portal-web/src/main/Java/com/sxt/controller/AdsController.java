package com.sxt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxt.domain.Content;
import com.sxt.service.ContentService;
import com.sxt.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author WWF
 * @title: AdsController
 * @projectName ego
 * @description: com.sxt.controller
 * @date 2019/5/27 8:28
 */
@RestController
public class AdsController {
    @Reference
    private ContentService contentService;
    @RequestMapping("/content/getAds")
    public Result getAds(){
        List<Content> ads = contentService.getAds();
        return Result.ok();
    }
}
