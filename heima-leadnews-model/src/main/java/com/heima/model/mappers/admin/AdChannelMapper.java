package com.heima.model.mappers.admin;

import com.heima.model.admin.pojos.AdChannel;

import java.util.List;

public interface AdChannelMapper {
    /**
     * 查询所有频道
     */
    public List<AdChannel> selectAll();

    AdChannel selectByPrimaryKey(Integer id);
}
