package com.sxt.controller;

import com.sxt.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WWF
 * @title: FileController
 * @projectName ego
 * @description: com.sxt.controller
 * @date 2019/5/24 20:49
 */
@RestController
public class FileController {
    @Value("${file.server}") //file.server 需要从properties 文件里面注入
    private String fileServerUrl;

    /**
     * 文件上传
     * @param uploadFile
     * @return
     */
    @PostMapping("/pic/upload")
    public Object uploadFile(MultipartFile uploadFile){
        //返回结果
        Map<String,Object> result=new HashMap<String, Object>();
        //	 如何将该文件写入到fastdfs 里面
        byte[] bytes=null;
        try {
            // 获取文件的字节
           bytes = uploadFile.getBytes();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //得到文件的原始名称
        String originalFilename = uploadFile.getOriginalFilename();
        //获取文件的后缀
        int indexOf = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(indexOf + 1, originalFilename.length());

        //上传文件
        String imgUrl= FastDFSUtil.uploadFile(bytes, ext);
        if (imgUrl!=null&&!imgUrl.equals("")){
            result.put("error",0);
            imgUrl=fileServerUrl+"/"+imgUrl;//文件访问路径
            result.put("url",imgUrl);
        }else {
            result.put("error",1);
            result.put("message","文件上传失败");
        }
       return result;
    }

}
