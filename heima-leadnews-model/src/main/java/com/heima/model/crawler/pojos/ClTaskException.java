package com.heima.model.crawler.pojos;

import lombok.Data;

import java.util.Date;
@Data
public class ClTaskException {
    private Integer id;
    private Integer taskId;
    private Integer no;
    private String url;
    private Boolean type;
    private Date createdTime;
    private String content;

}