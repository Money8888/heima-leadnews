package com.heima.model.media.pojos;

import lombok.Data;

import java.util.Date;
@Data
public class WmUserEquipment {
    private Integer id;
    private Integer userId;
    private Boolean type;
    private String version;
    private String sys;
    private String no;
    private Date createdTime;

}