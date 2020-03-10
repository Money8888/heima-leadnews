package com.heima.admin.apis;

import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;

public interface LoginControllerApi {
    public ResponseResult login(AdUser user);
}
