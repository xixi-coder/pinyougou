package com.pinyougou.sellergoods.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


@Service
public class BrandServiceImpl implements BrandService {
	
	@Autowired
     private TbBrandMapper tbBrandMapper;
	@Override
	public PageResult search(TbBrand tbBrand, int pagenum, int pagesize) {
		// TODO Auto-generated method stub
		TbBrandExample tbBrandExample =new TbBrandExample();
        Criteria criteria = tbBrandExample.createCriteria();
		
		if (tbBrand!=null) {
			if(tbBrand.getName()!=null&&tbBrand.getName().length()>0){
		            criteria.andNameLike("%"+tbBrand.getName()+"%");		
			}	
			if(tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0){
	            criteria.andNameLike("%"+tbBrand.getFirstChar()+"%");		
		}
		}
		
		PageHelper.startPage(pagenum, pagesize);
	    Page<TbBrand> page=	(Page<TbBrand>) tbBrandMapper.selectByExample(tbBrandExample);
	 
		return new PageResult(page.getTotal(), page.getResult());
	}
	@Override
	public void add(TbBrand tbBrand) {
		// TODO Auto-generated method stub
		tbBrandMapper.insert(tbBrand);
	}
	@Override
	public TbBrand findOne(long id) {
		// TODO Auto-generated method stub
		return tbBrandMapper.selectByPrimaryKey(id);
	}
	@Override
	public void save(TbBrand tbBrand) {
		// TODO Auto-generated method stub
		tbBrandMapper.updateByPrimaryKey(tbBrand);
	}
	@Override
	public void delete(long[] ids) {
		// TODO Auto-generated method stub
		for(long id : ids) {
			tbBrandMapper.deleteByPrimaryKey(id);
		}
	}
	public List<Map> selectOptionList(){
		return tbBrandMapper.selectOptionList();
	}
}
