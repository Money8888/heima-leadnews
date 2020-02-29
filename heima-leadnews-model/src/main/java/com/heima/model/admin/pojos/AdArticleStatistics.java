package com.heima.model.admin.pojos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class AdArticleStatistics {

    private Integer id;
    private Integer articleWeMedia;
    private Integer articleCrawlers;
    private Integer channelId;
    private Integer read20;
    private Integer read100;
    private Integer readCount;
    private Integer comment;
    private Integer follow;
    private Integer collection;
    private Integer forward;
    private Integer likes;
    private Integer unlikes;
    private Integer unfollow;
    private Date createdTime;


}