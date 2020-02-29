package com.heima.article;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ArticleJarApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticleJarApplication.class);
    }

    @Bean
    public ObjectMapper getMapper() {
        return new ObjectMapper();
    }
}
