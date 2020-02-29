package com.heima.model.admin.pojos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
public class AdChannel {
    private Integer id;
    private String name;
    private String description;
    private Boolean isDefault;
    private Boolean status;
    private Byte ord;
    private Date createdTime;


}