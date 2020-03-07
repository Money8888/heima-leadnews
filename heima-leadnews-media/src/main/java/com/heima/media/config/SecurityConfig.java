package com.heima.media.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置过滤
 * 在登录之后验证工作都是在这些过滤器中处理，比如把登录之后的用户放入线程中
 */
@Configuration
@ServletComponentScan("com.heima.common.web.wm.security")
public class SecurityConfig {
}
