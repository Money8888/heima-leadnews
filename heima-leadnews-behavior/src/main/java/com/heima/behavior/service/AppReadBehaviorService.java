package com.heima.behavior.service;

import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface AppReadBehaviorService {
    /**
     * 存储阅读数据
     * @param dto
     * @return
     */
    public ResponseResult saveReadBehavior(ReadBehaviorDto dto);
}
