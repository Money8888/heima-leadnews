package com.heima.model.media.pojos;

import lombok.Data;

@Data
public class WmNewsMaterial {
    private Integer id;
    private Integer materialId;
    private Integer newsId;
    private Boolean type;
    private Boolean ord;

}