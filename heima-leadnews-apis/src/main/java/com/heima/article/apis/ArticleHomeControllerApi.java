package com.heima.article.apis;

import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ArticleHomeControllerApi {
    /**
     * 加载首页文章
     */
    public ResponseResult load(ArticleHomeDto dto);
    /**
     * 加载更多文章
     */
    public ResponseResult loadMore(ArticleHomeDto dto);
    /**
     * 加载最新文章
     */
    public ResponseResult loadNew(ArticleHomeDto dto);
}
