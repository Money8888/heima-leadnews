package com.heima.admin.service;

import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;

public interface UserLoginService {
    /**
     * 登录
     * @param user
     * @return
     */
    ResponseResult login(AdUser user);
}
