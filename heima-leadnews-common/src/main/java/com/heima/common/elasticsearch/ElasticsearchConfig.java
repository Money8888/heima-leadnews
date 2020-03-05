package com.heima.common.elasticsearch;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix="spring.elasticsearch.jest")
@PropertySource("classpath:elasticsearch.properties")
public class ElasticsearchConfig {

    private String url;
    private Integer readTimeout;
    private Integer connectionTimeout;

    @Bean
    public JestClient getJestClient(){
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(this.url)
                .multiThreaded(true)
                .connTimeout(this.connectionTimeout)
                .readTimeout(this.readTimeout)
                .build());
        return factory.getObject();
    }
}
