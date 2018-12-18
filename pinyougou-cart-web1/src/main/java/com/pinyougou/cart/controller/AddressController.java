package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Reference
    private AddressService addressService;

    /**
     * 获取当前登陆人的地址列表
     * @return
     */
    @RequestMapping("findListByLoginUser")
    public List<TbAddress> findListByLoginUser(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TbAddress> list = addressService.findListByUserId(name);
        return list;
    }
}
