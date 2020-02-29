package com.heima.model.admin.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class AdRole {
    private Integer id;
    private String name;
    private String description;
    private Boolean isEnable;
    private Date createdTime;

}