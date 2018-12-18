package com.pinyougou.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;


import entity.Result;
import util.CookieUtil;

@RestController
@RequestMapping("/cart")
public class CartController {


  	@Reference(timeout=6000)
	private CartService cartService;
  	@Autowired
	private HttpServletRequest request;
  	@Autowired
	private HttpServletResponse response;

	/**
	 * 获取购物车列表
	 * @return
	 */
	@RequestMapping("findCartList")
	public List<Cart> findCartList(){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		String cartListstr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
		if (cartListstr==null||cartListstr.equals("")){
			cartListstr="[]";
		}
		List<Cart> cartList_cookie = JSON.parseArray(cartListstr, Cart.class);
		if (username.equals("anonymousUser")){//如果用户未登陆,从本地cookie中取购物车数据
			return cartList_cookie;
		}else {//如果用户已登陆,从redis中取购物车数据
			List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
			if (cartList_cookie.size()>0){//如果本地cookie中存在数据
				  //将cookie中的数据合并到redis数据中
				 cartList_redis = cartService.mergeCartList(cartList_redis, cartList_cookie);
				 //清除cookie
				 CookieUtil.deleteCookie(request,response,"cartList");
				 //将合并后的购物车数据存到redis中
				cartService.saveCartListToRedis(username,cartList_redis);
			}
			return cartList_redis;
		}

	}

	/**
	 * 添加商品到购物车
	 * @param itemId
	 * @param num
	 * @return
	 */
	@CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
	@RequestMapping("addGoodsToCartList")
	public Result addGoodsToCartList(Long itemId,Integer num){
//		//通过服务器端返回带有 Access-Control-Allow-Origin 标识的 Response header，用来解决资源的跨域权限问题
//		response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
//		//CORS 请求默认不发送 Cookie 和 HTTP 认证信息,设置此头信息表示:服务器同意接受cookie
//		response.setHeader("Access-Control-Allow-Credentials","true");
		List<Cart> cartList = findCartList();
		cartList=cartService.addGoodsToCartList(cartList,itemId,num);
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("当前登陆用户"+username);
		try {
			if (username.equals("anonymousUser")){//如果用户未登陆,数据存到cookie中
				CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"UTF-8");
				System.out.println("向cookie中存数据");
			}else {//如果已登陆,数据保存到redis中
				cartService.saveCartListToRedis(username,cartList);
				System.out.println("向redis中存数据");

			}
			return new Result(true,"新增成功");
		}catch (Exception e){
		e.printStackTrace();
			return new Result(false,"新增失败");
		}
	}

	
}
