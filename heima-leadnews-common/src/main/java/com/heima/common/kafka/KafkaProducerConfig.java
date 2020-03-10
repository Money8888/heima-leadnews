package com.heima.common.kafka;

import lombok.Data;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@EnableKafka
@ConfigurationProperties(prefix="kafka")
@PropertySource("classpath:kafka.properties")
public class KafkaProducerConfig {
    private static final int MAX_MESSAGE_SIZE = 16* 1024 * 1024;
    private String hosts;

    @Autowired(required = false)
    private ProducerListener<String, String> producerListener;

    @Bean
    public DefaultKafkaProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.getHosts());
        props.put(ProducerConfig.RETRIES_CONFIG, 10);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 5_000);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG,3*MAX_MESSAGE_SIZE);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,3*MAX_MESSAGE_SIZE);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 256 * 1024);
        return new DefaultKafkaProducerFactory<>( props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory producerFactory) {
        KafkaTemplate<String, String> t = new KafkaTemplate<>(producerFactory);
        if (this.producerListener != null) {
            t.setProducerListener(this.producerListener);
        }
        return t;
    }
}