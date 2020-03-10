package com.heima.media.kafka;

import com.heima.common.kafka.KafkaSender;
import com.heima.common.kafka.messages.SubmitArticleAuthMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AdminMessageSender {

    @Autowired
    private KafkaSender kafkaSender;


    /**
     * 异步发送行为消息
     * @param message
     */
    @Async
    public void sendMessage(SubmitArticleAuthMessage message){
        kafkaSender.sendSubmitArticleAuthMessage(message);
    }
}
