package com.heima.model.article.pojos;

import com.heima.model.annotation.IdEncrypt;
import lombok.Data;

@Data
public class ApArticleConfig {
    private Long id;
    // 增加注解，JSON序列化时自动混淆加密
//    @JsonIgnore
    @IdEncrypt
    private Integer articleId;
    private Boolean isComment;
    private Boolean isForward;
    private Boolean isDown;
    private Boolean isDelete;
}