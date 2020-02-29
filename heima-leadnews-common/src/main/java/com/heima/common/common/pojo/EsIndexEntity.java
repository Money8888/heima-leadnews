package com.heima.common.common.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class EsIndexEntity {

    private Long id;
    private String content;
    private Long channelId;
//    private Date pub_time;
    private Date publishTime;
    private Long status;
    private String title;
    private Long userId;
    private String tag;
}
