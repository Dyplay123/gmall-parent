package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.CategoryFeignClient;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    CategoryFeignClient categoryFeignClient;


    @GetMapping({"/","index"})
    public String indexPage(Model model){

        Result<List<CategoryTreeTo>> allCategoryWithTree = categoryFeignClient.getAllCategoryWithTree();
        if (allCategoryWithTree.isOk()){
            List<CategoryTreeTo> data = allCategoryWithTree.getData();
            model.addAttribute("list",data);
        }
        return "index/index";//页面的逻辑视图名

    }
}
