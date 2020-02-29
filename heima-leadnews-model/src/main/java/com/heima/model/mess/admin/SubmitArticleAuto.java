package com.heima.model.mess.admin;

import lombok.Data;

@Data
public class SubmitArticleAuto {

    // 文章类型
    private ArticleType type;
    // 文章ID
    private Integer articleId;

    public enum ArticleType{
        WEMEDIA,CRAWLER;
    }

}
