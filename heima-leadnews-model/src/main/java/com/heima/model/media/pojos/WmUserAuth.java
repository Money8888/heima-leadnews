package com.heima.model.media.pojos;

import lombok.Data;

@Data
public class WmUserAuth {
    private Integer id;
    private Integer userId;
    private Boolean type;
    private String name;

}