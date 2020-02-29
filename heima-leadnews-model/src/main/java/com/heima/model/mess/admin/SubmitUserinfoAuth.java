package com.heima.model.mess.admin;

import lombok.Data;

@Data
public class SubmitUserinfoAuth {

    // 资料类型类型
    private UserInfoType type;
    // 用户ID
    private Integer userId;

    public enum UserInfoType{
      IDENTITY,REALNAME;
    }

}
