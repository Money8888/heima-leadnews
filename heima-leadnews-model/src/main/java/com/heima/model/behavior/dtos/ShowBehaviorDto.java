package com.heima.model.behavior.dtos;


import com.heima.model.annotation.IdEncrypt;
import com.heima.model.article.pojos.ApArticle;
import lombok.Data;

import java.util.List;

@Data
public class ShowBehaviorDto {

    // 设备ID
    @IdEncrypt
    Integer equipmentId;
    List<ApArticle> articleIds;

}
