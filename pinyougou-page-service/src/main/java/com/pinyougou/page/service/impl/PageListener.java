package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;

@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage message1= (TextMessage) message;
        try {
            String text = message1.getText();
            System.out.println("监听获取消息"+text);
            itemPageService.genItemHtml(Long.parseLong(text));
            System.out.println("静态页面生成成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
