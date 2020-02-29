package com.heima.model.mess.app;

import lombok.Data;

@Data
public class UpdateArticle {

    // 修改文章的字段类型
    private UpdateArticleType type;
    // 文章ID
    private Integer articleId;
    // 修改数据的增量，可为正负
    private Integer add;
    private Integer apUserId;
    private Integer behaviorId;

    public enum UpdateArticleType{
        COLLECTION,COMMENT,LIKES,VIEWS;
    }
}
