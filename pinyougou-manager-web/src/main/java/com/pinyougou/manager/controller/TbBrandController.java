package com.pinyougou.manager.controller;


import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("brand")
public class TbBrandController {
   @Reference
   private BrandService brandService;
   
   /**
    * 显示所有,分页,条件查询,
    * @param tbBrand
    * @param pagenum
    * @param pagesize
    * @return
    */
   @RequestMapping("search")
   public PageResult search(@RequestBody TbBrand tbBrand,int pagenum,int pagesize){
	   
	   return brandService.search(tbBrand, pagenum, pagesize);
   }
   /**
    * 增加
    * @param tbBrand
    * @return
    */
   @RequestMapping("add")
   public Result add(@RequestBody TbBrand tbBrand){
	  try {
		brandService.add(tbBrand);
		return new Result(true, "增加成功");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return new Result(false, "增加失败");
	}
   }
   /**
    * 修改 返回到页面的数据
    * @param id
    * @return
    */
   @RequestMapping("findOne")
   public TbBrand findOne(long id) {
	   return brandService.findOne(id);
	   
   }
   
   /**
    * 修改 保存数据
    * @param tbBrand
    * @return
    */
   @RequestMapping("save")
   public Result save(@RequestBody TbBrand tbBrand) {
	   try {
		brandService.save(tbBrand);
		return new Result(true, "修改成功");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return new Result(false, "修改失败");
	}   }
   /**
    * 选择删除
    * @param ids
    * @return
    */
   @RequestMapping("delete")
   public Result delete(@RequestBody long [] ids) {
	try {
		brandService.delete(ids);
		return new Result(true, "删除成功");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return new Result(true, "删除失败");
	} }

	/**
	 * 返回下拉品牌列表
	 * @return
	 */
	@RequestMapping("selectOptionList")
	public List<Map> selectOptionList(){
		return brandService.selectOptionList();
	}
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
   }
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
