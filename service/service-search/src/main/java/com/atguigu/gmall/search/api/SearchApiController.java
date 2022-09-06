package com.atguigu.gmall.search.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("/api/inner/rpc/search")
@RestController
public class SearchApiController {

    @Resource
    GoodsService goodsService;

    //保存商品信息到es
    @PostMapping("/goods")
    public Result saveGoods(@RequestBody Goods goods) {
        goodsService.saveGoods(goods);
        return Result.ok();
    }
    //从es中删除信息
    @DeleteMapping("/goods/{skuId}")
    public Result deleteGoods(@PathVariable("skuId") Long skuId) {
        goodsService.deleteGoods(skuId);
        return Result.ok();
    }


    @PostMapping("/goods/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParamVo paramVo){
        SearchResponseVo responseVo = goodsService.search(paramVo);
        return Result.ok(responseVo);
    }
}
