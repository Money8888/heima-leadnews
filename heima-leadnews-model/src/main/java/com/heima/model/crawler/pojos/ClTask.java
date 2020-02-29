package com.heima.model.crawler.pojos;

import lombok.Data;

import java.util.Date;
@Data
public class ClTask {
    private Integer id;
    private String name;
    private String code;

    /**
     * cron 表达式
     */
    private String cron;

    /**
     * spring bean name
     */
    private String beanName;

    /**
     * 方法名称
     */
    private String method;
    private Integer intervalTime;
    private Boolean isEnable;
    private Date lastTime;
    private Date createdTime;

}