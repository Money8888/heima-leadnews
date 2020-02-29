package com.heima.model.user.pojos;

import com.heima.model.annotation.IdEncrypt;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApUserRealname {
    private Integer id;
    @IdEncrypt
    private Integer userId;
    private String name;
    private String idno;
    private String fontImage;
    private String backImage;
    private String holdImage;
    private String liveImage;
    private Short status;
    private String reason;
    private Date createdTime;
    private Date submitedTime;
    private Date updatedTime;


}