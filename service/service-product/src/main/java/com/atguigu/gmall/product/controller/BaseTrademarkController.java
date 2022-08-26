package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class BaseTrademarkController {
    @Autowired
    BaseTrademarkService baseTrademarkService;



    ///baseTrademark/{page}/{limit}
    //获取品牌分页列表
    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result baseTrademark(@PathVariable("page") Long page,
                                @PathVariable("limit") Long limit){

        Page<BaseTrademark> baseTrademarkPage = new Page<>(page,limit);
        Page<BaseTrademark> pageResult = baseTrademarkService.page(baseTrademarkPage);

        return Result.ok(pageResult);
    }
    // 添加品牌
    @PostMapping("/baseTrademark/save")
    public Result save(@RequestBody BaseTrademark trademark){
        baseTrademarkService.save(trademark);
        return Result.ok();

    }

    //3、修改品牌
    @PutMapping("/baseTrademark/update")
    public Result update(@RequestBody BaseTrademark trademark){
        baseTrademarkService.updateById(trademark);
        return Result.ok();

    }

    //4、删除品牌
    @DeleteMapping("baseTrademark/remove/{id}")
    public Result delete(@PathVariable("id") Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();

    }

    //5、根据Id获取品牌ffd
    @GetMapping("/baseTrademark/get/{id}")
    public Result getBaseTrademark(@PathVariable("id") Long id){
        BaseTrademark trademark = baseTrademarkService.getById(id);

        return Result.ok(trademark);

    }


    //3、获取品牌属性
    @GetMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> list = baseTrademarkService.list();
        return Result.ok(list);
    }
}
