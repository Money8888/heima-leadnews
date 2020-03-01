package com.heima.behavior.service.impl;

import com.heima.behavior.service.AppLikesBehaviorService;
import com.heima.common.zookeeper.Sequences;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApLikesBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mappers.app.ApBehaviorEntryMapper;
import com.heima.model.mappers.app.ApLikesBehaviorMapper;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.common.BurstUtils;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@SuppressWarnings("all")
public class AppLikesBehaviorServiceImpl implements AppLikesBehaviorService {

    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;

    @Autowired
    private ApLikesBehaviorMapper apLikesBehaviorMapper;

    @Autowired
    private Sequences sequences;

    @Override
    public ResponseResult saveLikesBehavior(LikesBehaviorDto dto) {
        // 获取用户信息或设备id（未登录时）
        ApUser user = AppThreadLocalUtils.getUser();
        if(user == null && dto.getEquipmentId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        // 根据当前用户信息或设备id查询行为实体 ap_behavior_entry
        Long userId = null;
        if(user != null){
            userId = user.getId();
        }
        ApBehaviorEntry apBehaviorEntry = apBehaviorEntryMapper.selectByUserIdOrEquipmentId(userId, dto.getEquipmentId());
        if(apBehaviorEntry==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApLikesBehavior alb = new ApLikesBehavior();
        alb.setId(sequences.sequenceApLikes());
        alb.setBehaviorEntryId(apBehaviorEntry.getId());
        alb.setCreatedTime(new Date());
        alb.setEntryId(dto.getEntryId());
        alb.setType(dto.getType());
        alb.setOperation(dto.getOperation());
        alb.setBurst(BurstUtils.encrypt(alb.getId(),alb.getBehaviorEntryId()));
        int insert = apLikesBehaviorMapper.insert(alb);
        return ResponseResult.okResult(insert);
    }
}
