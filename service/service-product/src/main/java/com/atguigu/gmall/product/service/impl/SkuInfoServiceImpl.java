package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
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
    @Resource
    RedissonClient redissonClient;
    @Resource
    BaseTrademarkService baseTrademarkService;

    @Resource
    SearchFeignClient searchFeignClient;

//sku上架
    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        skuInfo.setIsSale(1);

        skuInfoMapper.updateById(skuInfo);

        //2、给es中保存这个商品，商品就能被检索到了
        Goods goods = getGoodsBySkuId(skuId);
        searchFeignClient.saveGoods(goods);


    }



    //sku下架
    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        skuInfo.setIsSale(0);

        skuInfoMapper.updateById(skuInfo);


        searchFeignClient.deleteGoods(skuId);
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

        //把这个SkuId放到布隆过滤器中
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        filter.add(skuId);

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

    @Override
    public List<Long> findAllSkuId() {

        return skuInfoMapper.getAllSkuId();
    }



    private Goods getGoodsBySkuId(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        Goods goods = new Goods();
        goods.setId(skuId);
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setCreateTime(new Date());
        goods.setTmId(skuInfo.getTmId());


        BaseTrademark trademark = baseTrademarkService.getById(skuInfo.getTmId());
        goods.setTmName(trademark.getTmName());
        goods.setTmLogoUrl(trademark.getLogoUrl());


        Long category3Id = skuInfo.getCategory3Id();
        CategoryViewTo view = baseCategory3Mapper.getCategoryView(category3Id);
        goods.setCategory1Id(view.getCategory1Id());
        goods.setCategory1Name(view.getCategory1Name());
        goods.setCategory2Id(view.getCategory2Id());
        goods.setCategory2Name(view.getCategory2Name());
        goods.setCategory3Id(view.getCategory3Id());
        goods.setCategory3Name(view.getCategory3Name());

        goods.setHotScore(0L); //TODO 根据热度评分更新，然后来决定排序



        //查当前sku所有平台属性名和值
        List<SearchAttr> attrs = skuAttrValueService.getSkuAttrNameAndValue(skuId);
        goods.setAttrs(attrs);


        return goods;
    }
}




