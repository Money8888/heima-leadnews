package com.heima.crawler.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * spring工具类
 * 通过该工具类可以进行获取bean的实例
 */
public class SpringUtil {

    private static ApplicationContext applicationContext;

    /**
     * 设置applicationContext
     * @param applicationContext
     * @throws BeansException
     */
    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

}