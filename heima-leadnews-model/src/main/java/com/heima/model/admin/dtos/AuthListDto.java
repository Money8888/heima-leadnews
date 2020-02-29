package com.heima.model.admin.dtos;

import lombok.Data;

@Data
public class AuthListDto {

    private Integer size;
    private Integer page;
//    private Map<String, Object> params;

    private Short status;
}
