package com.heima.model.user.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class ApUserIdentity {
    private Integer id;
    private Integer userId;
    private String name;
    private String idno;
    private String fontImage;
    private String backImage;
    private String holdImage;
    private String industry;
    /**
     * 状态  0 创建中   1 待审核   2 审核失败  9 审核通过
     */
    private Integer status;
    private String reason;
    private Date createdTime;
    private Date submitedTime;
    private Date updatedTime;

}