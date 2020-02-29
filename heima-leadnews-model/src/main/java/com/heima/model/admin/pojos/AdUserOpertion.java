package com.heima.model.admin.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class AdUserOpertion {
    private Integer id;
    private Integer userId;
    private Integer equipmentId;
    private String ip;
    private String address;
    private Integer type;
    private String description;
    private Date createdTime;

}