package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author dypiay
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2022-08-23 20:48:43
*/
public interface SpuInfoService extends IService<SpuInfo> {

    Page<SpuInfo> getSpuInfoPage(Page<SpuInfo> page1, Long category3Id);

    void saveSpuInfo(SpuInfo spuInfo);
}
