package com.heima.model.article.pojos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ApCommentRepay {
    private Integer id;
    private Integer authorId;
    private String authorName;
    private Integer commentId;
    private String content;
    private Integer likes;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String address;
    private Date createdTime;
    private Date updatedTime;

}