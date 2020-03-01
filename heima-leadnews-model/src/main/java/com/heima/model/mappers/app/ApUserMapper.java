package com.heima.model.mappers.app;

import com.heima.model.user.pojos.ApUser;

public interface ApUserMapper {
    ApUser selectById(Integer id);
}
