package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author dypiay
* @description 针对表【base_category2(二级分类表)】的数据库操作Service实现
* @createDate 2022-08-22 22:54:53
*/
@Service
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2>
    implements BaseCategory2Service{

    @Resource
    BaseCategory2Mapper baseCategory2Mapper;

    @Override
    public List<BaseCategory2> getCategory1Child(Long category1Id) {


        LambdaQueryWrapper<BaseCategory2> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(BaseCategory2::getCategory1Id,category1Id);
        List<BaseCategory2> category2List = baseCategory2Mapper.selectList(lambdaQuery);
        return category2List;
    }

    @Override
    public List<CategoryTreeTo> getAllCategoryWithTree() {

     return    baseCategory2Mapper.getAllCategoryWithTree();
    }
}




