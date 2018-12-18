package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品 SKU ID 查询 SKU 商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item==null){
            throw  new RuntimeException("商品信息有误");
        }
        if (!"1".equals(item.getStatus())){
            throw  new RuntimeException("商品状态错误");
        }
        //2.获取商家 ID
        String sellerId = item.getSellerId();
        //3.根据商家 ID 判断购物车列表中是否存在该商家的购物车
        Cart cart = searchCartBySellerId(sellerId ,cartList);
        if (cart==null){   //4.如果购物车列表中不存在该商家的购物车
            //4.1 新建购物车对象
           cart= new Cart();
           cart.setSellerId(sellerId);
           cart.setSellerName(item.getSeller());
           //4.1.1传入item和数量,创建订单
           TbOrderItem orderItem = createOrderItem(item, num);
           //4.1.2创建订单列表,将订单对象添加到订单列表中
            ArrayList<TbOrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //4.2 将新建的购物车对象添加到购物车列表
            cartList.add(cart);
        }else {
        //5.如果购物车列表中存在该商家的购物车
        // 判断购物车中是否存在该商品
                 TbOrderItem orderItem= searchOrderItemByItemId(cart.getOrderItemList(),itemId);
        //5.1. 如果没有，新增商品订单
        if (orderItem==null){
            TbOrderItem orderItem1 = createOrderItem(item, num);
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            orderItemList.add(orderItem1);
        }else {
        //5.2. 如果有，在原购物车明细上添加数量，更改金额
         orderItem.setNum(orderItem.getNum()+num);
         orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
         //如果商品订单数量为0,则移除该商品订单
        if (orderItem.getNum()==0){
            cart.getOrderItemList().remove(orderItem);
        }
        //如果购物车中商品订单为0,则移除该购物车
        if (cart.getOrderItemList().size()==0){
            cartList.remove(cart);
        }
        }
        }
                return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从缓存中提出购物车数据");
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList==null){
            cartList=new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 将购物车保存到 redis
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<Cart> cartList){
        System.out.println("向缓存中存入购物车数据");
     redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    /**
     * 用户登陆后合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //将cartlist2中的订单存在cartlist1中
                addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList1;
    }

    ;

    /**
     * 判断购物车明细列表中是否存在该商品
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().equals(itemId)){
               return orderItem;
            }
        }
        return null;
    }

    /**
     * 根据商家id在查询是否有该商家的购物车
     * @param sellerId
     * @param cartList
     * @return
     */
    private Cart searchCartBySellerId(String sellerId,List<Cart> cartList){

        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    /**
     * 根据sku和数量创建订单
     * @param item
     * @param num
     * @return
     */
  private  TbOrderItem createOrderItem(TbItem item,Integer num){
      if (num<1){
          throw new RuntimeException("数量不合法");
      }
      TbOrderItem orderItem = new TbOrderItem();
      orderItem.setGoodsId(item.getGoodsId());
      orderItem.setItemId(item.getId());
      orderItem.setNum(num);
      orderItem.setPicPath(item.getImage());
      orderItem.setPrice(item.getPrice());
      orderItem.setTitle(item.getTitle());
      orderItem.setTotalFee(new BigDecimal(num * item.getPrice().doubleValue()));
      orderItem.setSellerId(item.getSellerId());
      return orderItem;
  }
}
