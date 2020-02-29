package com.heima.behavior.service.impl;

import com.heima.behavior.service.AppShowBehaviorService;
import com.heima.model.behavior.dtos.ShowBehaviorDto;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApShowBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mappers.app.ApBehaviorEntryMapper;
import com.heima.model.mappers.app.ApShowBehaviorMapper;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@SuppressWarnings("all")
public class AppShowBehaviorServiceImpl implements AppShowBehaviorService {

    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;

    @Autowired
    private ApShowBehaviorMapper apShowBehaviorMapper;

    @Override
    public ResponseResult saveShowBehavior(ShowBehaviorDto dto) {
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
        // 获取根据前台传递过来的文章id
        Integer[] articleIds = new Integer[dto.getArticleIds().size()];
        for(int i = 0; i < articleIds.length; i++){
            articleIds[i] = dto.getArticleIds().get(i).getId();
        }
        // 根据行为实体id和文章列表id查询行为表 ap_show_behavior
        List<ApShowBehavior> apShowBehaviors = apShowBehaviorMapper.selectListByEntryIdAndArticleIds(apBehaviorEntry.getEntryId(), articleIds);
        // 过滤数据删除表中已经存在的文章id
        List<Integer> integers = Arrays.asList(articleIds); // 转换成List方便删除
        if(!apShowBehaviors.isEmpty()){
            apShowBehaviors.forEach(items ->{
                // 取到从行为表中查出来的文章id
                Integer articleId = items.getArticleId();
                // 从文章id中删除该id
                integers.remove(articleId);
            });

        }
        // 保存操作
        if(!integers.isEmpty()){
            articleIds = new Integer[integers.size()];
            integers.toArray(articleIds);
            apShowBehaviorMapper.saveShowBehavior(articleIds, apBehaviorEntry.getId());
        }

        return null;
    }
}
