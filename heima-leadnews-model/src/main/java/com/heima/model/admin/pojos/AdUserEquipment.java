package com.heima.model.admin.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class AdUserEquipment {
    private Integer id;
    private Integer userId;
    private Boolean type;
    private String version;
    private String sys;
    private String no;
    private Date createdTime;

}