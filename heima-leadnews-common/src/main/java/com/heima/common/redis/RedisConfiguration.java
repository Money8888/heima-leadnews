package com.heima.common.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@PropertySource("classpath:redis.properties")
public class RedisConfiguration extends RedisAutoConfiguration {
}
