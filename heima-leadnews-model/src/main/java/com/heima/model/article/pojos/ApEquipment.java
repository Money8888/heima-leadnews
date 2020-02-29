package com.heima.model.article.pojos;

import lombok.Data;

import java.util.Date;
@Data
public class ApEquipment {
    private Integer id;
    private Boolean type;
    private String version;
    private String sys;
    private String no;
    private Date createdTime;

}