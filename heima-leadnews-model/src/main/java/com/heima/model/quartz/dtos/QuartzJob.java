package com.heima.model.quartz.dtos;

import lombok.Data;

import java.io.Serializable;
@Data
public class QuartzJob implements Serializable {
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 分组名称
     */
    private String jobGroup;

    /**
     * 类名称
     */
    private String beanId;
    /**
     * 执行的方法
     */
    private String method;
    /**
     * cron 表达式
     */
    private String cronExpression;
    /**
     * 重复时间
     */
    private Long repeatTime;

    /**
     * 是否是cron表达式
     */
    private Boolean cronJob;
    /**
     * 调度名称
     */
    private String schedulerName;

}
