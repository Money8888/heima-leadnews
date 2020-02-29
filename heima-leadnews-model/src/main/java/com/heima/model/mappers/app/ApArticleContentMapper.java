package com.heima.model.mappers.app;

import com.heima.model.article.pojos.ApArticleContent;

public interface ApArticleContentMapper {
    ApArticleContent selectByArticleId(Integer articleId);
}
