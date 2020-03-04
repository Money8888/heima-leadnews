package com.heima.login.controller.v1;

import com.heima.login.apis.LoginControllerApi;
import com.heima.login.service.ApUserLoginService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.pojos.ApUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
public class ApUserLoginController implements LoginControllerApi {
    @Autowired
    private ApUserLoginService apUserLoginService;

    @Override
    public ResponseResult login(ApUser user) {
        return apUserLoginService.loginAuth(user);
    }
}
