package com.heima.behavior.service;

import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface AppLikesBehaviorService {
    /**
     * 存储喜欢数据
     * @param dto
     * @return
     */
    public ResponseResult saveLikesBehavior(LikesBehaviorDto dto);
}
