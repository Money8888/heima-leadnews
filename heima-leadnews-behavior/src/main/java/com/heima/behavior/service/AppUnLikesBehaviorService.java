package com.heima.behavior.service;

import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface AppUnLikesBehaviorService {
    /**
     * 存储不喜欢数据
     * @param dto
     * @return
     */
    public ResponseResult saveUnLikesBehavior(UnLikesBehaviorDto dto);
}
