package com.heima.model.media.pojos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class WmUserLogin {
    private Integer id;
    private Integer userId;
    private Integer equipmentId;
    private String ip;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Date createdTime;

}