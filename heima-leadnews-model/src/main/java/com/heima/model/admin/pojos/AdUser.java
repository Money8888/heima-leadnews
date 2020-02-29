package com.heima.model.admin.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class AdUser {

    private Long id;
    private String name;
    private String password;
    private String salt;
    private String nickname;
    private String image;
    private String phone;
    private Short status;
    private String email;
    private Date loginTime;
    private Date createdTime;
}