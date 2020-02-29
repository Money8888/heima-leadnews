package com.heima.model.crawler.pojos;

import lombok.Data;

import java.util.Date;

/**
 * 媒体
 */
@Data
public class ClMaterial {
    private Integer id;
    private Integer userId;
    private String url;
    private Boolean type;
    private Boolean isCollection;
    private Date createdTime;

}