package com.heima.admin.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.admin.service.ReviewMediaArticleService;
import com.heima.common.kafka.KafkaListener;
import com.heima.common.kafka.KafkaTopicConfig;
import com.heima.common.kafka.messages.SubmitArticleAuthMessage;
import com.heima.model.mess.admin.SubmitArticleAuto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class AutoReviewArticleListener implements KafkaListener<String, String> {

    @Autowired
    private KafkaTopicConfig kafkaTopicConfig;

    @Autowired
    private ReviewMediaArticleService reviewMediaArticleService;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public String topic() {
        return kafkaTopicConfig.getSubmitArticleAuth();
    }

    @Override
    public void onMessage(ConsumerRecord<String, String> consumerRecord, Consumer<?, ?> consumer) {
        String value = consumerRecord.value();
        log.info("接收到的消息为：{}"+value);
        try {
            SubmitArticleAuthMessage message = mapper.readValue(value, SubmitArticleAuthMessage.class);
            if(message != null){
                SubmitArticleAuto.ArticleType type = message.getData().getType();
                if(SubmitArticleAuto.ArticleType.WEMEDIA.equals(type)){
                    Integer articleId = message.getData().getArticleId();
                    if(articleId != null){
                        reviewMediaArticleService.autoReviewArticleByMedia(articleId);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("处理自动审核文章错误:[{}],{}",value,e.getMessage());
            throw new RuntimeException("WS消息处理错误",e);
        }
    }
}
