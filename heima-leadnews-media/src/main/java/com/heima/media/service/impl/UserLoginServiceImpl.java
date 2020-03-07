package com.heima.media.service.impl;

import com.heima.media.service.UserLoginService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mappers.wemedia.WmUserMapper;
import com.heima.model.media.pojos.WmUser;
import com.heima.utils.jwt.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@SuppressWarnings("all")
public class UserLoginServiceImpl  implements UserLoginService {

    @Autowired
    private WmUserMapper wmUserMapper;

    @Override
    public ResponseResult login(WmUser user) {
        if(StringUtils.isEmpty(user.getName()) || StringUtils.isEmpty(user.getPassword())){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户名或密码不能为空");
        }
        WmUser wmUser = wmUserMapper.selectByName(user.getName());
        if(wmUser != null){
            if(wmUser.getPassword().equals(user.getPassword())){
                Map<String,Object> result = new HashMap<>();
                wmUser.setPassword("");
                wmUser.setSalt("");
                result.put("token", AppJwtUtil.getToken(wmUser));
                result.put("user", wmUser);
                return ResponseResult.okResult(result);
            }else {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
        }else {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
        }
    }
}
