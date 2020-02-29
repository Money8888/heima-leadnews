package com.heima.article.apis;

import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ArticleInfoControllerApi {
    ResponseResult loadArticleInfo(ArticleInfoDto dto);
}
