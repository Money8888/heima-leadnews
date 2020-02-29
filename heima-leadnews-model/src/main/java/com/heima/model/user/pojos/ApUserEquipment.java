package com.heima.model.user.pojos;

import lombok.Data;

import java.util.Date;
@Data
public class ApUserEquipment {
    private Integer id;
    private Integer userId;
    private Integer equipmentId;
    private Date lastTime;
    private Date createdTime;

}