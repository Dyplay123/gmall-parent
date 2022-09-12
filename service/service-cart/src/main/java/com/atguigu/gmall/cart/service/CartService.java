package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;

import java.util.List;

public interface CartService {
    SkuInfo addToCart(Long skuId, Integer num);


    //根据用户登录信息决定用哪个购物车键
    String determinCartKey();

    //把指定商品添加到指定购物车
    SkuInfo addItemToCart(Long skuId, Integer num, String cartKey);

    //从购物车中获取某个商品
    CartInfo getItemFromCart(String cartKey, Long skuId);

    // 获取指定购物车中的所有商品。排好序（按照createTime顺序）。
    List<CartInfo> getCartList(String cartKey);
//更新购物车中某个商品的数量
    void updateItemNum(Long skuId, Integer num, String cartKey);

   //更新购物车中商品的勾选状态
    void updateChecked(Long skuId, Integer status, String cartKey);

   //删除购物车中商品
    void deleteCartItem(Long skuId, String cartKey);

   //删除购物车中选中的商品
    void deleteChecked(String cartKey);

  //获取指定购物车中所有选中的商品
    List<CartInfo> getCheckedItems(String cartKey);
//
   //合并购物车
    void mergeUserAndTempCart();
//

   //更新这个购物车中所有商品的价格
    void updateCartAllItemsPrice(String cartKey, List<CartInfo> cartInfos);
}
