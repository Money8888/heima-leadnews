package com.heima.common.kafka;

import lombok.Getter;
import lombok.Setter;

/**
 * Kafka消息
 */
public abstract class KafkaMessage<T> {

    // 尝试次数
    @Getter
    int retry;
    // 生成时间
    @Getter
    long time = System.currentTimeMillis();
    // 消息类型
    String type;
    // 消息实体数据
    @Setter
    @Getter
    T data;
    public KafkaMessage(){}
    public KafkaMessage(T data){
        this.data = data;
    }

    public void addRetry(){
        this.retry++;
    }
    // 获取消息类型
    public abstract String getType();
}