package com.heima.model.article.pojos;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ApHotWords implements Serializable {
    private Integer id;
    private String hotWords;
    private Integer type;
    private String hotDate;
    private Date createdTime;
}