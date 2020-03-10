package com.heima.common.kafka;

import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@EnableKafka
@ConfigurationProperties(prefix="kafka")
@PropertySource("classpath:kafka.properties")
public class KafkaConsumerConfig {
    private static final int CONCURRENCY = 8;
    public final static Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    String hosts;
    String group;


    @Bean("defaultKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(RetryErrorHandler retryErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setRetryTemplate(this.buildRetryTemplate());
        factory.setErrorHandler(retryErrorHandler);
        factory.getContainerProperties().setAckOnError(false);
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(buildComsumerConfig()));
        factory.setConcurrency(KafkaConsumerConfig.CONCURRENCY);
        return factory;
    }

    protected Map<String, Object> buildComsumerConfig() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.getHosts());
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, this.group);
        propsMap.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,8 * 1024 * 1024);
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 90_000);
        return propsMap;
    }

    private RetryTemplate buildRetryTemplate() {
        RetryTemplate t = new RetryTemplate();
        ExponentialBackOffPolicy backOff = new ExponentialRandomBackOffPolicy();
        backOff.setInitialInterval(1000L);
        t.setBackOffPolicy(backOff);
        t.setRetryPolicy(new SimpleRetryPolicy(5));
        t.registerListener(new RetryListenerSupport() {
            @Override
            public <T, E extends Throwable> void onError(RetryContext context,
                                                         RetryCallback<T, E> callback, Throwable throwable) {
                KafkaConsumerConfig.LOGGER.warn("Retry processing Kafka message "
                        + context.getRetryCount() + " times", throwable);
            }
        });
        return t;
    }

}