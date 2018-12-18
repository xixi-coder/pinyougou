package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.Arrays;

@Component
public class ItemDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage message1= (ObjectMessage) message;
        try {
            Long[] ids = (Long[]) message1.getObject();
            System.out.println("监听获得消息"+ids);
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
            System.out.println("删除索引库成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
