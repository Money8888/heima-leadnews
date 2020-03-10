package com.heima.admin.service.impl.commfilter;

import com.heima.model.admin.dtos.CommonDto;
import com.heima.model.admin.dtos.CommonWhereDto;
import com.heima.model.admin.pojos.AdUser;

/**
 * 通用过滤器的过滤类
 */
public interface BaseCommonFilter {
    void doListAfter(AdUser user, CommonDto dto);
    void doUpdateAfter(AdUser user,CommonDto dto);
    void doInsertAfter(AdUser user, CommonDto dto);
    void doDeleteAfter(AdUser user, CommonDto dto);

    /**
     * 获取更新字段里面的值
     * @param field
     * @param dto
     * @return
     */
    default CommonWhereDto findUpdateValue(String field, CommonDto dto){
        if(dto != null){
            for (CommonWhereDto cwd : dto.getSets()) {
                if(field.equals(cwd.getFiled())){
                    return cwd;
                }
            }
        }
        return null;
    }

    default CommonWhereDto findWhereValue(String field, CommonDto dto){
        if(dto != null){
            for (CommonWhereDto cwd : dto.getWhere()) {
                if(field.equals(cwd.getFiled())){
                    return cwd;
                }
            }
        }
        return null;
    }
}
