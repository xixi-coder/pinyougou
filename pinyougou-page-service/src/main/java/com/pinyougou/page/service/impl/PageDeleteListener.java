package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;


@Component
public class PageDeleteListener implements MessageListener {

    @Autowired
   private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage message1= (ObjectMessage) message;
        try {
            Long[] ids = (Long[]) message1.getObject();
            System.out.println("监听获取订阅的消息"+ids.toString());
            boolean b = itemPageService.deleteItemHtml(ids);
            System.out.println("静态页面删除结果:"+b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
