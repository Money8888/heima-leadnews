package com.heima.model.behavior.dtos;

import com.heima.model.annotation.IdEncrypt;
import lombok.Data;

@Data
public class ReadBehaviorDto {
    // 设备ID
    @IdEncrypt
    Integer equipmentId;
    // 文章、动态、评论等ID
    @IdEncrypt
    Integer articleId;

    /**
     * 阅读次数
     */
    Short count;

    /**
     * 阅读时长（S)
     */
    Integer readDuration;

    /**
     * 阅读百分比
     */
    Short percentage;

    /**
     * 加载时间
     */
    Short loadDuration;

}
