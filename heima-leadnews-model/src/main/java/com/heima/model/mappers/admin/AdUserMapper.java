package com.heima.model.mappers.admin;

import com.heima.model.admin.pojos.AdUser;

public interface AdUserMapper {
    AdUser selectByName(String name);
}
