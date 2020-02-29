package com.heima.model.crawler.pojos;

import lombok.Data;

import java.util.Date;
@Data
public class ClIpPool {
    private Integer id;
    private String supplier;
    private String ip;

    /**
     * 端口号
     */
    private int port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 耗时
     */
    private Integer duration;

    /**
     * 错误信息
     */
    private String error;
    private Boolean isEnable;
    private String ranges;
    private Date createdTime;

}