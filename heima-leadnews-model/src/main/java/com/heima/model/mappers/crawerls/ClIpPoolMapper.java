package com.heima.model.mappers.crawerls;

import com.heima.model.crawler.pojos.ClIpPool;

import java.util.List;

public interface ClIpPoolMapper {
   
    int deleteByPrimaryKey(Integer id);

    int insert(ClIpPool record);

    int insertSelective(ClIpPool record);

    ClIpPool selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ClIpPool record);

    int updateByPrimaryKey(ClIpPool record);

    /**
     * 查询所有数据
     *
     * @param record
     * @return
     */
    List<ClIpPool> selectList(ClIpPool record);

    /**
     * 查询可用的列表
     *
     * @param record
     * @return
     */
    List<ClIpPool> selectAvailableList(ClIpPool record);
}