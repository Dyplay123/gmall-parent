package com.atguigu.gmall.model.to;

import lombok.Data;

import java.util.List;

@Data
public class CategoryTreeTo {
    private  Long categoryId; //1
    private String categoryName;
    private List<CategoryTreeTo> categoryChild;//子分类

}
