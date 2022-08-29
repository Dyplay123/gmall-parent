package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
* @author dypiay
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2022-08-23 20:48:43
*/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Resource
    SkuInfoMapper skuInfoMapper;

    @Resource
    SkuImageService skuImageService;
    @Resource
    SkuAttrValueService skuAttrValueService;
    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    BaseCategory3Mapper baseCategory3Mapper;
    @Resource
    SpuSaleAttrService spuSaleAttrService;

//sku上架
    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        skuInfo.setIsSale(1);

        skuInfoMapper.updateById(skuInfo);
    }
   //sku下架
    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        skuInfo.setIsSale(0);

        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public void saveSkuInfo(SkuInfo info) {

        //1、sku基本信息保存到 sku_info
        save(info);
        Long skuId = info.getId();

        //2、sku的图片信息保存到 sku_image
        for (SkuImage skuImage : info.getSkuImageList()) {
            skuImage.setSkuId(skuId);
        }
        skuImageService.saveBatch(info.getSkuImageList());

        //3、sku的平台属性名和值的关系保存到 sku_attr_value
        List<SkuAttrValue> attrValueList = info.getSkuAttrValueList();
        for (SkuAttrValue attrValue : attrValueList) {
            attrValue.setSkuId(skuId);
        }
        skuAttrValueService.saveBatch(attrValueList);

        //4、sku的销售属性名和值的关系保存到 sku_sale_attr_value
        List<SkuSaleAttrValue> saleAttrValueList = info.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue : saleAttrValueList) {
            saleAttrValue.setSkuId(skuId);
            saleAttrValue.setSpuId(info.getSpuId());
        }
        skuSaleAttrValueService.saveBatch(saleAttrValueList);

    }

    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo skuDetailTo = new SkuDetailTo();
        //0,先把skuInfo查出来
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        //3查出skuImage
        List<SkuImage> skuImages =   skuImageService.getSkuImage(skuId);
        skuInfo.setSkuImageList(skuImages);

        //1查询出三级分类的信息
        CategoryViewTo categoryViewTo = baseCategory3Mapper.getCategoryView(skuInfo.getCategory3Id());
        skuDetailTo.setCategoryView(categoryViewTo);

       //2skuInfo的基本信息
        skuDetailTo.setSkuInfo(skuInfo);

        //(√) 实时价格查询
        BigDecimal price = get1010Price(skuId);
        skuDetailTo.setPrice(price);

       //sku对应的spu所有销售属性名和值
        List<SpuSaleAttr> saleAttrList = spuSaleAttrService
                .getSaleAttrAndValueMarkSku(skuInfo.getSpuId(),skuId);
        skuDetailTo.setSpuSaleAttrList(saleAttrList);

        //获取 118|120 这样的json字符串
        Long spuId = skuInfo.getSpuId();
       String valueJson  = spuSaleAttrService.getAllSkuSaleAttrValueJson(spuId);
        skuDetailTo.setValuesSkuJson(valueJson);

        return skuDetailTo;
    }
    //分开查询skuInfo信息
    @Override
    public SkuInfo getDetailSkuInfo(Long skuId) {
        //0,先把skuInfo查出来
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo;
    }
   //分开查询skuImage 的信息
    @Override
    public List<SkuImage> getDetailSkuImages(Long skuId) {
        List<SkuImage> skuImages =   skuImageService.getSkuImage(skuId);
        return skuImages;
    }

    public BigDecimal get1010Price(Long skuId) {
        BigDecimal price = skuInfoMapper.getRealPrice(skuId);
        return price;
    }
}




