package com.heima.admin.service;

public interface ReviewMediaArticleService {
    /**
     * 自媒体端发布文章审核
     * @param newsId 文章id
     */
    public void autoReviewArticleByMedia(Integer newsId);
}
