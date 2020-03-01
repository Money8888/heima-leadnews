package com.heima.user.apis;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;

public interface UserRelationControllerApi {
    ResponseResult follow(UserRelationDto dto);
}
