package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import java.util.*;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public HashMap<String,Object> search(Map searchmap) {
         HashMap<String,Object> map = new HashMap<>();
         //关键字空格处理
        String keywords = (String) searchmap.get("keywords");
        searchmap.put("keywords",keywords.replace(" ",""));

        //1.根据关键词查询,高亮显示搜索的关键词
        Map map1 = searchList(searchmap);
        map.putAll(map1);
        //2.根据关键词在solr中以item_category分组查询商品分类
        List<String> categoryList = searchCategoryList(searchmap);
        map.put("categoryList",categoryList);
        //3.根据分类名称 查询对应的品牌列表和规格列表
        //如果过滤条件的category不为空,根据过滤条件的分类名称查询品牌和规格列表
        if (!"".equals(searchmap.get("category"))){
                map.putAll(searchBrandAndSpecList((String) searchmap.get("category")));
            }else {
                //没有过滤条件时,会对搜索结果进行以分类名称进行solr分组,
                // 默认根据第一个分类名称查询查询对应的品牌列表和规格列表
                if (categoryList.size()>0){
                    map.putAll(searchBrandAndSpecList(categoryList.get(0)));
                }
        }
        System.out.println(map);
        return map;
    }

    /**
     * 查询列表数据
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap){
        Map  map = new HashMap();
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //设置高亮的域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //为查询对象设置高亮选项
        query.setHighlightOptions(highlightOptions);
        //1.1按关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //1.2按分类筛选
        if (!"".equals(searchMap.get("category"))){
            Criteria categoryCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery categoryfilterQuery = new SimpleFilterQuery(categoryCriteria);
            query.addFilterQuery(categoryfilterQuery);
        }
        //1.3按品牌过滤
        if (!"".equals(searchMap.get("brand"))){
            Criteria brandCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery brandFilterQuery = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(brandFilterQuery);
        }
        //1.4按规格过滤
       if (searchMap.get("spec")!=null){
           Map<String,String> specMap = (Map) searchMap.get("spec");
           for (String key : specMap.keySet()) {
               Criteria filtercriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
               SimpleFilterQuery categoryFilterQuery = new SimpleFilterQuery(filtercriteria);
               query.addFilterQuery(categoryFilterQuery);
           }
    }

       //1.5按价格过滤
        if(!"".equals(searchMap.get("price"))){
            String pricestr = (String) searchMap.get("price");
            String[] price = pricestr.split("-");
            //如果价格区间数组的第一个不是0,应该大于或等于第一个数
            if (!"0".equals(price[0])){

                Criteria item_price = new Criteria("item_price").greaterThanEqual(price[0]);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(item_price);
                query.addFilterQuery(filterQuery);
            }
            //如果价格区间数组的第二个不是*,应该小于或等于第二个数
            if (!"*".equals(price[1])){
                Criteria item_price = new Criteria("item_price").lessThanEqual(price[1]);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(item_price);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.6分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo==null){
            //默认当前页是第一页
            pageNo=1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize==null){
            //默认每页显示20个数据
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize);//分页开始的索引
        query.setRows(pageSize);//每页显示的记录数
        //1.7排序
        String sortField= (String) searchMap.get("sortField");//排序字段
        String sortValue= (String) searchMap.get("sort");//排序顺序
        if (sortValue!=null&&!"".equals(sortValue)){
            //升序
            if (sortValue.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            //降序
            if (sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //page.getHighlighted()是高亮入口的集合
        //搜索结果数据的循环
        for (HighlightEntry<TbItem> h : page.getHighlighted()) {
            //获取高亮列表(高亮域的个数) 此处只有一个高亮域
            TbItem item = h.getEntity();
            if (h.getHighlights().size()>0&&h.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows",page.getContent());
        map.put("total",page.getTotalElements());//总记录数
        map.put("totalPages",page.getTotalPages());//总页数
        return map;
    }

    /**
     * 分组查询  根据搜索的关键字查询分类列表
     * @param searchMap
     * @return
     */
    private List<String> searchCategoryList(Map searchMap){
        List<String> list = new ArrayList();
        SimpleQuery query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组的域
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据分组的域(列)得到分组的结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取分组入口集合
        List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : entryList) {
            list.add(entry.getGroupValue());
        }
        return list;
    }

    /**
     * 查询根据分类名称查询品牌列表和规格列表
     * @param category
     * @return
     */
    @Autowired
    private RedisTemplate redisTemplate;
    private HashMap<String,Object> searchBrandAndSpecList(String category){
        HashMap<String, Object> map = new HashMap<>();
        //根据分类名称在缓存中查询到对应的模板id
        Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if(templateId!=null){
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("brandList",brandList);
            map.put("specList",specList);
        }
        return map;
    }

    /**
     * 导入更新的商品数据至solr索引库
     * @param list
     */
    public void importList(List list){
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 根据下架的商品id在索引库中删除数据
     * @param goodsIdList
     */
    @Override
    public void deleteByGoodsIds(List goodsIdList) {
         SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);;
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    };
}
