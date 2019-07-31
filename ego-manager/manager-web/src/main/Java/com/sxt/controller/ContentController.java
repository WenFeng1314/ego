package com.sxt.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sxt.domain.Content;
import com.sxt.model.Page;
import com.sxt.service.ContentService;
import com.sxt.vo.EasyUIDataGrid;
import com.sxt.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author WWF
 * @title: ContentController
 * @projectName ego
 * @description: com.sxt.controller
 * @date 2019/5/26 22:19
 */
@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;
    @RequestMapping("/getData")
    public EasyUIDataGrid getData(
            @RequestParam(required = true) Integer categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer rows){
        Page<Content> pages = contentService.findByPage(categoryId, page, rows);

       return new EasyUIDataGrid(pages.getTotal(),pages.getResults());
    }

    /**
     * 添加和修改
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(Content content){
        if (content.getId()==null){
            //添加
            //设置初始化
            content.setCreated(new Date());
            content.setUpdated(new Date());
            contentService.add(content);
            return Result.ok();

        }else {
            // 修改
            //修改之前，由于使用了富文本编辑器，所有回显，要通过id去单独查询
         content.setUpdated(new Date());
         contentService.update(content);
         return  Result.ok();

        }

    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    public Result deleteContent(String ids){
         contentService.batchDeleteContent(ids);
       return Result.ok();
    }

}
