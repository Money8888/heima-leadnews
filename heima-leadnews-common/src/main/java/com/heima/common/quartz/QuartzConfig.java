package com.heima.common.quartz;

import lombok.Data;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.quartz.JobStoreType;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.util.Date;

/**
 * 1、每个服务一套Quart表，自动增加前缀groupPrefix
 * 2、每组groupPrefix的任务，命名符合自动管理的（AutoJobDetail结尾），将自动清理和增加、更新
 * 3、支持集群内单点初始化（必要squartzScanJobDetail），setOverwriteExistingJobs必须设置成false
 * 4、生产环境自动按照分支号生成和清理JOB注册
 * 5、启动后自动扫描AbstractJob的子类并注册成调度器
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.quartz")
@PropertySource("classpath:quartz.properties")
@Import(QuartzAutoConfiguration.class)
public class QuartzConfig {

    String groupPrefix;
    String schedulerName;

    /**
     * 重要Bean，务删
     * @return
     */
    @Bean
    @Primary
    public QuartzProperties quartzProperties(){
        QuartzProperties quartzProperties = new QuartzProperties();
        quartzProperties.setJobStoreType(JobStoreType.JDBC);
        quartzProperties.setSchedulerName(getSchedulerName());
        // 必须是false
        quartzProperties.setOverwriteExistingJobs(false);
        quartzProperties.getProperties().put("org.quartz.scheduler.instanceId",getSchedulerName()+"-instanceId");
        quartzProperties.getProperties().put("org.quartz.jobStore.class","org.quartz.impl.jdbcjobstore.JobStoreTX");
        // 优化死锁配置
        quartzProperties.getProperties().put("org.quartz.jobStore.txIsolationLevelSerializable","true");
        quartzProperties.getProperties().put("org.quartz.threadPool.threadCount","30");
        quartzProperties.getProperties().put("org.quartz.jobStore.tablePrefix",groupPrefix.toUpperCase()+"QRTZ_");
        quartzProperties.getProperties().put("org.quartz.jobStore.isClustered","true");
        return quartzProperties;
    }

    /**
     * 重要Bean，务删
     * @return
     */
    @Bean
    public JobDetailFactoryBean quartzScanJobDetail(){
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setGroup("init-group-"+getGroupPrefix()+"0");
        jobDetailFactoryBean.setRequestsRecovery(true);
        jobDetailFactoryBean.setBeanName("quartzScanJobDetail");
        jobDetailFactoryBean.setJobClass(QuartzScanJob.class);
        jobDetailFactoryBean.setDescription("初始化集群任务");
        return  jobDetailFactoryBean;
    }

    /**
     * 重要Bean，务删
     * 定义一个Trigger
     * @return
     */
    @Bean
    public SimpleTriggerFactoryBean scanJobDetailCronTrigger(@Qualifier("quartzScanJobDetail") JobDetail quartzScanJobDetail){
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(quartzScanJobDetail);
        trigger.setStartTime(new Date());
        trigger.setRepeatCount(0);
        trigger.setStartDelay(500);
        trigger.setRepeatInterval(1);
        return  trigger;
    }
}