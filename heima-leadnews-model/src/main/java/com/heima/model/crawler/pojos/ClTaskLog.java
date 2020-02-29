package com.heima.model.crawler.pojos;

import lombok.Data;

import java.util.Date;
@Data
public class ClTaskLog {
    private Integer id;
    private Integer taskId;
    private Integer no;
    private Integer count;
    private Boolean isSuccess;
    private Date startTime;
    private Date endTime;

}