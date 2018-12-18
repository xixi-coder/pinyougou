package com.pinyougou.page.service.impl;


import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfigurer  freeMarkerConfigurer;
    @Override
    public boolean genItemHtml(Long goodsId)  {
        try {
            //创建配置类
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //获取模板
            Template template =configuration.getTemplate("item.ftl");
            HashMap<Object, Object> dataModel = new HashMap<>();
            //1.将商品放入数据模型
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods",tbGoods);
            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //2.将商品描述数据放入数据模型
            dataModel.put("goodsDesc",tbGoodsDesc);
            //3.获取商品的三级分类名称
            String itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            dataModel.put("itemCat1",itemCat1);
            dataModel.put("itemCat2",itemCat2);
            dataModel.put("itemCat3",itemCat3);
            //4.sku列表
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
            example.setOrderByClause("is_default desc");//按照默认状态降序
            List<TbItem> tbItems = this.itemMapper.selectByExample(example);
            dataModel.put("itemList",tbItems);
            //创建字符输出流对象
            FileWriter fileWriter = new FileWriter(pagedir+goodsId+".html");
            //输出静态原型
            template.process(dataModel,fileWriter);
            fileWriter.close();
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteItemHtml(Long[] goodsId){
        try {
            for (Long id : goodsId) {
                File file = new File(pagedir + id + ".html");
                file.delete();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
