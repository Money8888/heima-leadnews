package com.heima.model.user.pojos;

import com.heima.model.annotation.IdEncrypt;
import lombok.Data;

import java.util.Date;

@Data
public class ApUserSearch {
    private Integer id;
    @IdEncrypt
    private Integer entryId;
    private String keyword;
    private Integer status;
    private Date createdTime;

}