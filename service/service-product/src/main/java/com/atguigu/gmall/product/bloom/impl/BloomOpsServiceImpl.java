package com.atguigu.gmall.product.bloom.impl;

import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloomOpsServiceImpl implements BloomOpsService {
    @Autowired
    RedissonClient redissonClient;
    /*
    * 重建布隆过滤器
    *
    * */
    @Override
    public void rebuildBloom(String bloomName, BloomDataQueryService bloomDataQueryService) {

        //得到原本的布隆过滤器
        RBloomFilter<Object> oldBloomFilter = redissonClient.getBloomFilter(bloomName);
        //准备一个新的布隆过滤器，将所有的东西都初始化好
        String newBloomName = bloomName + "_new";
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(newBloomName);

      //拿到所有商品的id
       List list =  bloomDataQueryService.queryData();
       //初始化新的布隆
        bloomFilter.tryInit(5000000,0.0001);
        for (Object skuId : list) {
            bloomFilter.add(skuId);
        }
        //ob bb nb
        //两个交换，nb变成ob
        oldBloomFilter.rename("bbbb_bloom");
        bloomFilter.rename(bloomName);

        //删除老布隆，和中间层交换
        oldBloomFilter.deleteAsync();
        redissonClient.getBloomFilter("bbbb_bloom").deleteAsync();



    }
}
