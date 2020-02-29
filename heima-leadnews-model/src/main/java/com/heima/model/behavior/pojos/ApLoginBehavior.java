package com.heima.model.behavior.pojos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class ApLoginBehavior {
    private Integer id;
    private Integer entryId;
    private Integer equipmentId;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Date createdTime;

}