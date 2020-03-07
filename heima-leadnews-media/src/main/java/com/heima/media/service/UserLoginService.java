package com.heima.media.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.pojos.WmUser;

public interface UserLoginService {
    ResponseResult login(WmUser user);
}
