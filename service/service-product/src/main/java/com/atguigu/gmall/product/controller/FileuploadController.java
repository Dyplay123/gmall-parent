package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;

import com.atguigu.gmall.product.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/product")
public class FileuploadController {

    @Autowired
    FileUploadService fileUploadService;


    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestParam("file")MultipartFile file) throws Exception {
       //文件上传后返回url地址页面
        String url = fileUploadService.upload(file);
        return Result.ok(url);
    }
}
