package com.heima.model.user.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class ApUserChannel {
    private Integer id;
    private Integer channelId;
    private Integer userId;
    private String name;
    private Short ord;
    private Date createdTime;
}