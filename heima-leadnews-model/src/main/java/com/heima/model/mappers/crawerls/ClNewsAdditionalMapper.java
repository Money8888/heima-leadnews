package com.heima.model.mappers.crawerls;

import com.heima.model.crawler.pojos.ClNewsAdditional;

import java.util.Date;
import java.util.List;

public interface ClNewsAdditionalMapper {

    
    int deleteByPrimaryKey(Integer id);

    int insert(ClNewsAdditional record);

    
    int insertSelective(ClNewsAdditional record);

    
    ClNewsAdditional selectByPrimaryKey(Integer id);

    
    int updateByPrimaryKeySelective(ClNewsAdditional record);

   
    int updateByPrimaryKey(ClNewsAdditional record);


    /**
     * 按条件查询所有数据
     *
     * @param record
     * @return
     */
    List<ClNewsAdditional> selectList(ClNewsAdditional record);

    /**
     * 获取需要更新的数据
     * @return
     */
    List<ClNewsAdditional> selectListByNeedUpdate(Date currentDate);
}