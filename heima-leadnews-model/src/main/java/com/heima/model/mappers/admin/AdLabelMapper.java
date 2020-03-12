package com.heima.model.mappers.admin;

import com.heima.model.admin.pojos.AdLabel;

import java.util.List;

public interface AdLabelMapper {
    
    int deleteByPrimaryKey(Integer id);

    int insert(AdLabel record);
    
    int insertSelective(AdLabel record);

    AdLabel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AdLabel record);
    
    int updateByPrimaryKey(AdLabel record);

    List<AdLabel> queryAdLabelByLabels(List<String> labelList);

    List<AdLabel> queryAdLabelByLabelIds(List<String> labelList);
}