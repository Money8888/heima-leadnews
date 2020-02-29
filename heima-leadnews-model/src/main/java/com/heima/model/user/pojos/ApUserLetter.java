package com.heima.model.user.pojos;

import lombok.Data;

import java.util.Date;
@Data
public class ApUserLetter {
    private Integer id;
    private Integer userId;
    private Integer senderId;
    private String senderName;
    private String content;
    private Boolean isRead;
    private Date createdTime;
    private Date readTime;

}