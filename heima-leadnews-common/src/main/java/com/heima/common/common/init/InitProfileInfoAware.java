package com.heima.common.common.init;

import com.heima.common.common.contants.Contants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class InitProfileInfoAware implements ApplicationContextAware {
    Logger logger = LoggerFactory.getLogger(InitProfileInfoAware.class);

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
        // 在WEB环境才执行
        String temp[] =applicationContext.getEnvironment().getActiveProfiles();
        if(temp!=null&&temp.length>0) {
            Contants.PROFILE_NAME = temp[0];
            logger.info("当前的配置环境是：{}", Contants.PROFILE_NAME);
        }
    }
}
