package com.atguigu.gmall.item.service;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;

public interface SkuDetailService {


    SkuDetailTo getSkuDetail(Long skuId);

    void updateHotScore(Long skuId);
}
