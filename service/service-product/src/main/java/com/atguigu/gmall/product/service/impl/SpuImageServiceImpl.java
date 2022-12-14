package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SpuImage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.mapper.SpuImageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author dypiay
* @description 针对表【spu_image(商品图片表)】的数据库操作Service实现
* @createDate 2022-08-23 20:48:43
*/
@Service
public class SpuImageServiceImpl extends ServiceImpl<SpuImageMapper, SpuImage>
    implements SpuImageService{

    @Resource
    SpuImageMapper spuImageMapper;

    @Override
    public List<SpuImage> getspuImageList(Long spuId) {
        List<SpuImage> spuImages = spuImageMapper.selectList(new LambdaQueryWrapper<SpuImage>()
                .eq(SpuImage::getSpuId, spuId));
        return spuImages;
    }
}




