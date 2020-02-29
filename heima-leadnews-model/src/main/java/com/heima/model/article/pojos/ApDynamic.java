package com.heima.model.article.pojos;

import lombok.Data;

import java.util.Date;
@Data
public class ApDynamic {
    private Integer id;
    private Integer userId;
    private String userName;
    private String content;
    private Boolean isForward;
    private Integer articleId;
    private String articelTitle;
    private String articleImage;
    private Boolean layout;
    private String images;
    private Integer likes;
    private Integer collection;
    private Integer comment;
    private Integer views;
    private Date createdTime;

}