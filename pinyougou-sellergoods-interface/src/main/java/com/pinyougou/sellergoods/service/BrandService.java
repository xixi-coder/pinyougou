package com.pinyougou.sellergoods.service;



import com.pinyougou.pojo.TbBrand;

import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

public interface BrandService {
    /**
     * 分页,条件查询
     */
	PageResult search(TbBrand tbBrand,int pagenum,int pagesize);
	
	/**
	 * 增加
	 * @param tbBrand
	 * @return
	 */
	void add(TbBrand tbBrand);
	
	/**
	 * 修改(返回的到页面数据)
	 * 
	 */
	TbBrand findOne(long id);
	/**
	 * 保存修改的数据
	 */
	
	void save(TbBrand tbBrand);
	
	/**
	 * 删除
	 */
	
	void delete(long[] ids);

	/**
	 * 下拉品牌列表
	 */
	List<Map> selectOptionList();
}
