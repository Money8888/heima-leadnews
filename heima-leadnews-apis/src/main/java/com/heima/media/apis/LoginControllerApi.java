package com.heima.media.apis;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.pojos.WmUser;

public interface LoginControllerApi {
    public ResponseResult login(WmUser user);
}
