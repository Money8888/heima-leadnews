package com.heima.model.user.pojos;

import lombok.Data;

import java.util.Date;
@Data
public class ApUserInfo {
    private Integer id;
    private Integer userId;
    private String name;
    private String idno;
    private String company;
    private String occupation;
    private Byte age;
    private Date birthday;
    private String introduction;
    private String location;
    private Integer fans;
    private Integer follows;
    private Boolean isRecommendMe;
    private Boolean isRecommendFriend;
    private Boolean isDisplayImage;
    private Date updatedTime;

}