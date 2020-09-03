package com.heima.common.quartz;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Data
@Log4j2
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Transactional
public class QuartzScanJob extends QuartzJobBean {

    @Value("branch-${info.git.branch?:default}")
    String branch;
    @Value("${spring.quartz.group-prefix}")
    String groupPrefix;
    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;
    @Autowired
    DefaultListableBeanFactory defaultListableBeanFactory;
    @Autowired
    private Scheduler scheduler;
    // 描述器后缀
    private static final String DETAIL_SUFFIX = "AutoJobDetail";
    // 触发器后缀
    private static final String TRIGGER_SUFFIX = "AutoTrigger";

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String temp = (String)jobExecutionContext.getJobDetail().getJobDataMap().get("branch");
        log.info("当前程序环境是[{}]，变量环境是：[{}]",getBranch(),temp);
        if(!branch.equalsIgnoreCase(temp)) {
            Map<String, AbstractJob> abs = defaultListableBeanFactory.getBeansOfType(AbstractJob.class);
            if(abs!=null){
                this.clearGroupJobAndTrigger(abs);
                for (String key : abs.keySet()) {
                    AbstractJob job = abs.get(key);
                    if(job.isAutoOverwrite()) {
                        String detailBeanName = key + DETAIL_SUFFIX;
                        createJobDetail(key, detailBeanName, job);
                        this.createdTrigger((JobDetail) defaultListableBeanFactory.getBean(detailBeanName), key, job);
                    }
                }
            }
            jobExecutionContext.getJobDetail().getJobDataMap().put("branch",getBranch());
        }else{
            log.info("============= skip auto init jobs");
        }
    }

    /**
     * 清理掉当前分组的JOB和触发器信息
     * @param abs
     */
    private void clearGroupJobAndTrigger(Map<String, AbstractJob> abs){
        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupStartsWith(getGroupPrefix()));
            for (JobKey jobKey : jobKeys) {
                String key = jobKey.getName().replace(TRIGGER_SUFFIX,"");
                AbstractJob job = abs.get(key);
                boolean isDelete = true;
                if(job!=null){
                    isDelete = job.isAutoOverwrite();
                }
                if(isDelete){
                    scheduler.deleteJob(jobKey);
                    log.info("auto manger clear job [{}]",jobKey);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 创建一个参数
     * @param beanName
     * @param job
     * @return
     */
    private void createJobDetail(String beanName,String detailBeanName,AbstractJob job){
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(JobDetailFactoryBean.class);
        definitionBuilder.addPropertyValue("jobClass",job.getClass());
        definitionBuilder.addPropertyValue("beanName",beanName);
        definitionBuilder.addPropertyValue("group",groupPrefix+job.group());
        definitionBuilder.addPropertyValue("durability",job.isComplateAfterDelete());
        definitionBuilder.addPropertyValue("description",job.descJob());
        definitionBuilder.addPropertyValue("requestsRecovery",job.isStartAutoRecovery());
        definitionBuilder.addPropertyValue("jobDataAsMap",job.initParam());
        defaultListableBeanFactory.registerBeanDefinition(detailBeanName,definitionBuilder.getBeanDefinition());
        log.info("success register jobdetail : [{}]",detailBeanName);
    }

    /**
     * 注册触发器
     * @param detail
     * @param beanName
     * @param job
     */
    private void createdTrigger(JobDetail detail,String beanName,AbstractJob job){
        String temp[] = job.triggerCron();
        String name = beanName+"Trigger";
        for (int i = 0; i < temp.length; i++) {
            String  triggerName = name+"_"+i;
            BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(CronTriggerFactoryBean.class);
            definitionBuilder.addPropertyValue("name",triggerName);
            definitionBuilder.addPropertyValue("group",groupPrefix+name);
            definitionBuilder.addPropertyValue("cronExpression",temp[i]);
            definitionBuilder.addPropertyValue("description",job.descTrigger());
            definitionBuilder.addPropertyValue("jobDetail",detail);
            defaultListableBeanFactory.registerBeanDefinition(triggerName,definitionBuilder.getBeanDefinition());
            try {
                scheduler.scheduleJob(detail, (Trigger) defaultListableBeanFactory.getBean(triggerName));
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("success register trigger : [{}]",triggerName);
        }
    }
}
