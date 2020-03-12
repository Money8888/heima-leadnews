package com.heima.crawler.service;

public interface AdLabelService {
    /**
     *  param:标签的列表，逗号分隔，从文章解析中获取具体的标签labels，去数据库中查询
     *  return : 返回的是标签的id  以逗号分隔
     */
    public String getLabelIds(String labels);
    /**
     *  param:   标签id
     *  return : 频道id  如果没有查到，为0  未分类
     */
    public Integer getAdChannelByLabelIds(String labelIds);
}
