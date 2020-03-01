package com.heima.article.service;

import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.common.dtos.ResponseResult;

public interface AppArticleInfoService {
    /**
     * 加载文章内容信息
     * @param articleId
     * @return
     */
    ResponseResult getArticleInfo(Integer articleId);

    /**
     * 加载文章初始的配置信息
     * @param dto
     * @return
     */
    ResponseResult loadArticleBehavior(ArticleInfoDto dto);
}
