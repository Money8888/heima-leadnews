package com.heima.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 处理异步请求的配置
 * 例如行为保存出错不会影响粉丝和关注数表发生改变
 */

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    private static final int corePoolSize = 10;           // 核心线程数（默认线程数）
    private static final int maxPoolSize = 100;              // 最大线程数
    private static final int keepAliveTime = 10;         // 允许线程空闲时间（单位：默认为秒）
    private static final int queueCapacity = 500;        // 缓冲队列数
    private static final String threadNamePrefix = "default-async"; // 线程池名前缀

    public ThreadPoolTaskExecutor taskExecutor(){
        // 创建线程池
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setThreadNamePrefix(threadNamePrefix);
        pool.setCorePoolSize(corePoolSize);
        pool.setMaxPoolSize(maxPoolSize);
        pool.setKeepAliveSeconds(keepAliveTime);
        pool.setQueueCapacity(queueCapacity);

        // 直接在execute方法的调用线程中运行
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        pool.initialize();
        return pool;
    }

}
