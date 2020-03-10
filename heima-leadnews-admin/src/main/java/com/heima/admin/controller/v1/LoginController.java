package com.heima.admin.controller.v1;

import com.heima.admin.service.UserLoginService;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController{

    @Autowired
    private UserLoginService userLoginService ;

    @RequestMapping("/in")
    public ResponseResult login(@RequestBody AdUser user){
        return userLoginService.login(user);
    }

}