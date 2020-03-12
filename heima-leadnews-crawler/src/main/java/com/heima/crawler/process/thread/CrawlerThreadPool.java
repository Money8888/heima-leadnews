package com.heima.crawler.process.thread;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池处理类
 */
@Log4j2
public class CrawlerThreadPool {
    /**
     * 线程池最大连接数 IO密集型 2n+1
     */

    private static final int threadNum = Runtime.getRuntime().availableProcessors();
    /**
     * 创建一个阻塞队列
     */
    private static final ArrayBlockingQueue queue = new ArrayBlockingQueue<Runnable>(10000);
    /**
     * 创建一个线程池
     * 核心线程数 1
     * 最大线程数 2n+1
     * 等待超时时间60秒
     * 阻塞队列大小 10000
     */
    private static final ExecutorService executorService = new ThreadPoolExecutor(1, threadNum,
            60L, TimeUnit.SECONDS, queue) {

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            log.info("线程池开始执行任务，threadName:{},线程池堆积数量：{}", t.getName(), queue.size());
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            log.info("线程池开始执行完成");
            if (null != t) {
                log.error(t.getLocalizedMessage());
            }
        }
    };


    /**
     * 提交一个线程
     *
     * @param runnable
     */
    public static void submit(Runnable runnable) {
        log.info("线程池添加任务,线程池堆积任务数量：{},最大线程数:{}", queue.size(), threadNum);
        executorService.execute(runnable);
    }


}