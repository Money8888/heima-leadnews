package com.heima.user.service.impl;

import com.heima.model.behavior.dtos.FollowBehaviorDto;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApFollowBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mappers.app.ApBehaviorEntryMapper;
import com.heima.model.mappers.app.ApFollowBehaviorMapper;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.service.AppFollowBehaviorService;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@SuppressWarnings("all")
public class AppFollowBehaviorServiceImpl implements AppFollowBehaviorService {

    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;

    @Autowired
    private ApFollowBehaviorMapper apFollowBehaviorMapper;

    /**
     * 异步处理
     * @param dto
     * @return
     */
    @Override
    @Async
    public ResponseResult saveFollowBehavior(FollowBehaviorDto dto) {
        int i = 1/0;
        ApUser user = AppThreadLocalUtils.getUser();
        Long userId = null;
        if(user != null){
            userId = user.getId();
        }
        if(userId == null && dto.getEquipmentId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        ApBehaviorEntry apBehaviorEntry = apBehaviorEntryMapper.selectByUserIdOrEquipmentId(userId, dto.getEquipmentId());
        if(apBehaviorEntry == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 保存行为数据
        ApFollowBehavior apFollowBehavior = new ApFollowBehavior();
        apFollowBehavior.setEntryId(apBehaviorEntry.getId());
        apFollowBehavior.setArticleId(dto.getArticleId());
        apFollowBehavior.setFollowId(dto.getFollowId());
        apFollowBehavior.setCreatedTime(new Date());

        // 返回插入条数
        int insert = apFollowBehaviorMapper.insert(apFollowBehavior);
        return ResponseResult.okResult(insert);
    }
}
