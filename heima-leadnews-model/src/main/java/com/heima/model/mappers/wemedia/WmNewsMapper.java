package com.heima.model.mappers.wemedia;

import com.heima.model.media.pojos.WmNews;

public interface WmNewsMapper {
    /**
     * 根据主键修改
     * @param wmNews
     * @return
     */
    int updateByPrimaryKey(WmNews wmNews);
    /**
     * 添加草稿新闻
     * @param dto
     * @return
     */
    int insertNewsForEdit(WmNews dto);
}
