package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SpuImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author dypiay
* @description 针对表【spu_image(商品图片表)】的数据库操作Service
* @createDate 2022-08-23 20:48:43
*/
public interface SpuImageService extends IService<SpuImage> {

    List<SpuImage> getspuImageList(Long spuId);
}
