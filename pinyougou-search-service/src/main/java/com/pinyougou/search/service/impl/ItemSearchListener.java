package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class ItemSearchListener implements MessageListener {

    @Autowired
    private  ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        TextMessage message1= (TextMessage) message;
        try {
            String text = message1.getText();
            System.out.println("监听到消息"+text);
            List<TbItem> list = JSON.parseArray(text, TbItem.class);
            itemSearchService.importList(list);
            System.out.println("更新索引库成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
