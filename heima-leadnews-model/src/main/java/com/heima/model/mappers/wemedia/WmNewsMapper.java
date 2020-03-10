package com.heima.model.mappers.wemedia;

import com.heima.model.media.dtos.WmNewsPageReqDto;
import com.heima.model.media.pojos.WmNews;

import java.util.List;

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

    /**
     * 查询根据dto条件
     * @param dto
     * @param uid
     * @return
     */
    List<WmNews> selectBySelective(WmNewsPageReqDto dto, Long uid);

    /**
     * 查询总数统计
     * @param dto
     * @param uid
     * @return
     */
    int countSelectBySelective(WmNewsPageReqDto dto, Long uid);

    /**
     * 根据id查询文章详情
     * @param id
     * @return
     */
    WmNews selectNewsDetailByPrimaryKey(Integer id);

    /**
     * 删除文章
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WmNews record);
}
