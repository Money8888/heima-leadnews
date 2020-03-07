package com.heima.media.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置jackson解析
 */
@Configuration
@ComponentScan({"com.heima.common.common.init", "com.heima.common.fastdfs"})
public class InitConfig {
}
