package com.heima.user.service;

import com.heima.model.behavior.dtos.FollowBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface AppFollowBehaviorService {
    /**
     * 保存关注行为
     */
    public ResponseResult saveFollowBehavior(FollowBehaviorDto dto);
}
