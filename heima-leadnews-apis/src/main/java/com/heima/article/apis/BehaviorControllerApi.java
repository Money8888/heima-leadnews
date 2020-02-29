package com.heima.article.apis;

import com.heima.model.behavior.dtos.ShowBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface BehaviorControllerApi {
    ResponseResult saveShowBehavior(ShowBehaviorDto dto);
}
