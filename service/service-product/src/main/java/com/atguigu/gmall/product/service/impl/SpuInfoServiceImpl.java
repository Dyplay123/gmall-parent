package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SpuInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dypiay
 * @description 针对表【spu_info(商品表)】的数据库操作Service实现
 * @createDate 2022-08-23 20:48:43
 */
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
        implements SpuInfoService {

    @Resource
    SpuInfoMapper spuInfoMapper;

    @Resource
    SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    SpuImageMapper spuImageMapper;
    @Resource
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Resource
    BaseSaleAttrMapper baseSaleAttrMapper;

    @Override
    public Page<SpuInfo> getSpuInfoPage(Page<SpuInfo> page1, Long category3Id) {

        LambdaQueryWrapper<SpuInfo> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(SpuInfo::getCategory3Id, category3Id);
        Page<SpuInfo> spuInfoPage = spuInfoMapper.selectPage(page1, lambdaQuery);
        return spuInfoPage;
    }
    @Transactional
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        spuInfoMapper.insert(spuInfo);
        //销售属性集合，如果不为空，就添加进去
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();

        if (spuImageList.size() > 0) {
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            }

        }

        //商品属性集合，如果不为空，就添加进去

        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList.size() > 0) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);

                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList.size() > 0) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);


                    }

                }
            }

        }


    }


}




