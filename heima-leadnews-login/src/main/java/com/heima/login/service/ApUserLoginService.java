package com.heima.login.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.pojos.ApUser;

public interface ApUserLoginService {
    /**
     * 用户登录验证
     * @param user
     * @return
     */
    ResponseResult loginAuth(ApUser user);
}
