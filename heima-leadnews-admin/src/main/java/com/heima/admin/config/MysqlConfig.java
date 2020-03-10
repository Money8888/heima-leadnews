package com.heima.admin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.heima.common.mysql.core")
@MapperScan("com.heima.admin.dao")
public class MysqlConfig {
}
