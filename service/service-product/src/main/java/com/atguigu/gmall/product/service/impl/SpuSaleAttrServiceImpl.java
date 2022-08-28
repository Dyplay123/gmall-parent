package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.ValueSkuJsonTo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author dypiay
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
* @createDate 2022-08-23 20:48:43
*/
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
    implements SpuSaleAttrService{
    @Resource
    SpuSaleAttrMapper spuSaleAttrMapper;



    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
       List<SpuSaleAttr> list = spuSaleAttrMapper.getSpuSaleAttrList(spuId);
        return list;
    }

    @Override
    public List<SpuSaleAttr> getSaleAttrAndValueMarkSku(Long spuId, Long skuId) {
        return  spuSaleAttrMapper.getSaleAttrAndValueMarkSku(spuId,skuId);

    }
  //查询所有sku value 属性组合
    @Override
    public String getAllSkuSaleAttrValueJson(Long spuId) {
      List<ValueSkuJsonTo> valueSkuJsonTos  =  spuSaleAttrMapper.getAllSkuSaleAttrValueJson(spuId);
        Map<String,Long> map = new HashMap<>();
      for (ValueSkuJsonTo valueSkuJsonTo : valueSkuJsonTos) {
            Long skuId = valueSkuJsonTo.getSkuId();
            String valueJson = valueSkuJsonTo.getValueJson();
          map.put(valueJson,skuId);
        }
        String json = Jsons.toStr(map);
        return json;
    }
}




