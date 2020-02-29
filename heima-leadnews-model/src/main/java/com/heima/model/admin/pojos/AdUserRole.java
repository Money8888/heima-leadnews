package com.heima.model.admin.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class AdUserRole {
    private Integer id;
    private Integer roleId;
    private Integer userId;
    private Date createdTime;

}