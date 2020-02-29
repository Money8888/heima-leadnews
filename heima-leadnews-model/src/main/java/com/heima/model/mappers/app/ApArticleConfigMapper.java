package com.heima.model.mappers.app;

import com.heima.model.article.pojos.ApArticleConfig;

public interface ApArticleConfigMapper {
    ApArticleConfig selectByArticleId(Integer articleId);
}
