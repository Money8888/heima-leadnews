package com.heima.model.article.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class ApAssociateWords {
    private Integer id;
    private String associateWords;
    private Date createdTime;
}