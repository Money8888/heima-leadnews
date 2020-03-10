package com.heima.common.kafka.messages;

import com.heima.common.kafka.KafkaMessage;
import com.heima.model.mess.admin.SubmitArticleAuto;

public class SubmitArticleAuthMessage extends KafkaMessage<SubmitArticleAuto> {

    public SubmitArticleAuthMessage(){}
    public SubmitArticleAuthMessage(SubmitArticleAuto data){
        super(data);
    }

    @Override
    public String getType() {
        return "submit-article-auth";
    }
}
