package com.heima.model.admin.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class AdRoleAuth {

    private Integer id;
    private Integer roleId;
    private Boolean type;
    private Integer entryId;
    private Date createdTime;

}