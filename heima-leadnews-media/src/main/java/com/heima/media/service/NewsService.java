package com.heima.media.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.dtos.WmNewsDto;

public interface NewsService {
    /**
     * 自媒体发布文章
     * @param wmNews
     * @return
     */
    ResponseResult saveNews(WmNewsDto wmNews, Short type);
}
