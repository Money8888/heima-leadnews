package com.heima.model.mappers.crawerls;

import com.heima.model.crawler.pojos.ClNewsComment;

import java.util.List;

public interface ClNewsCommentMapper {

    
    int deleteByPrimaryKey(Integer id);

   
    int insert(ClNewsComment record);

    
    int insertSelective(ClNewsComment record);

    
    ClNewsComment selectByPrimaryKey(Integer id);

    
    int updateByPrimaryKeySelective(ClNewsComment record);

    
    int updateByPrimaryKey(ClNewsComment record);


    /**
     * 按条件查询所有数据
     *
     * @param record
     * @return
     */
    List<ClNewsComment> selectList(ClNewsComment record);
}