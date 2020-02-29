package com.heima.model.crawler.pojos;

import lombok.Data;

/**
 * 媒体文章关联
 */
@Data
public class ClNewsMaterial {
    private Integer id;
    private Integer materialId;
    private Integer newsId;
    private Boolean type;
    private Boolean ord;

}