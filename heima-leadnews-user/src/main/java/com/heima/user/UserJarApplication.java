package com.heima.user;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserJarApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserJarApplication.class);
    }

    @Bean
    public ObjectMapper getMapper() {
        return new ObjectMapper();
    }
}
