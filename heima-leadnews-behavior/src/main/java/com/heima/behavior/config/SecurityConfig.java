package com.heima.behavior.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置过滤
 * 在登录之后验证工作都是在这些过滤器中处理，比如把登录之后的用户放入线程中
 */
@Configuration
@ComponentScan("com.heima.common.web.app.security")
public class SecurityConfig {
}
