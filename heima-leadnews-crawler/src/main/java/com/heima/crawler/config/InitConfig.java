package com.heima.crawler.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan({"com.heima.common.common.init", "com.heima.common.mysql.core", "com.heima.common.kafka"})
@EnableScheduling
public class InitConfig {
}
