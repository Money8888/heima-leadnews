package com.heima.model.mappers.wemedia;

import com.heima.model.media.pojos.WmUser;

public interface WmUserMapper {
    WmUser selectByName(String name);
}
