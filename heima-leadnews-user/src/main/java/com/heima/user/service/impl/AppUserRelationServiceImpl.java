package com.heima.user.service.impl;

import com.heima.common.zookeeper.Sequences;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.behavior.dtos.FollowBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mappers.app.ApAuthorMapper;
import com.heima.model.mappers.app.ApUserFanMapper;
import com.heima.model.mappers.app.ApUserFollowMapper;
import com.heima.model.mappers.app.ApUserMapper;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserFan;
import com.heima.model.user.pojos.ApUserFollow;
import com.heima.user.service.AppFollowBehaviorService;
import com.heima.user.service.AppUserRelationService;
import com.heima.utils.common.BurstUtils;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AppUserRelationServiceImpl  implements AppUserRelationService {

    @Autowired
    private ApAuthorMapper apAuthorMapper;

    @Autowired
    private ApUserMapper apUserMapper;

    @Autowired
    private ApUserFollowMapper apUserFollowMapper;

    @Autowired
    private ApUserFanMapper apUserFanMapper;

    @Autowired
    Sequences sequences;

    @Autowired
    AppFollowBehaviorService appFollowBehaviorService;

    @Override
    public ResponseResult follow(UserRelationDto dto) {
        if(dto.getOperation() == null || dto.getOperation() >1 || dto.getOperation() < 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "无操作！");
        }

        // 获取followId(被关注人的id)
        Integer followId = dto.getUserId();
        if(followId == null && dto.getAuthorId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE,"followId或authorId不能为空");
        }else if(followId == null){
            ApAuthor apAuthor = apAuthorMapper.selectById(dto.getAuthorId());
            if(apAuthor != null){
                followId = apAuthor.getUserId().intValue();
            }
        }
        if(followId == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"关注人不存在");
        }else {
            // 获取当前登录用户
            ApUser user = AppThreadLocalUtils.getUser();
            if(user != null){
                // 判断是否已经关注
                if(dto.getOperation() == 0){
                    // 表示未关注，需要关注
                    // 保存关注表(ap_user_follow)数据，粉丝表(ap_user_fan)数据和行为表(ap_follow_behavior)数据
                    return followByUserId(user, followId, dto.getArticleId());
                }else {
                    // 表示已关注，点击表示取消关注
                    // 删除关注表(ap_user_follow)数据，粉丝表(ap_user_fan)
                    return followCancelByUserId(user, followId);
                }
            }else {
                return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            }
        }

    }

    /**
     *
     * @param user 登录用户id
     * @param followId 被关注人id
     * @param articleId 文章id
     * @return
     */

    private ResponseResult followByUserId(ApUser user, Integer followId, Integer articleId) {

        // 判断被关注用户是否存在
        ApUser apUser = apUserMapper.selectById(followId);
        if(apUser == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"被关注用户不存在！");
        }
        // 判断是否已经保存
        ApUserFollow apUserFollow = apUserFollowMapper.selectByFollowId(BurstUtils.groudOne(user.getId()), user.getId(), followId);
        if(apUserFollow == null){
            // 保存粉丝表数据
            ApUserFan apUserFan = apUserFanMapper.selectByFansId(BurstUtils.groudOne(followId), followId, user.getId());
            if(apUserFan == null){
                apUserFan = new ApUserFan();
                apUserFan.setId(sequences.sequenceApUserFan());
                apUserFan.setUserId(followId);
                apUserFan.setFansId(user.getId());
                apUserFan.setFansName(user.getName());
                apUserFan.setLevel((short) 0);
                apUserFan.setIsDisplay(true);
                apUserFan.setIsShieldComment(false);
                apUserFan.setIsShieldLetter(false);
                apUserFan.setBurst(BurstUtils.encrypt(apUserFan.getId(), apUserFan.getUserId()));
                apUserFanMapper.insert(apUserFan);
            }
            // 保存关注表数据
            apUserFollow = new ApUserFollow();
            apUserFollow.setId(sequences.sequenceApUserFollow());
            apUserFollow.setUserId(user.getId());
            apUserFollow.setFollowId(followId);
            apUserFollow.setFollowName(apUser.getName());
            apUserFollow.setCreatedTime(new Date());
            apUserFollow.setLevel((short) 0);
            apUserFollow.setIsNotice(true);
            apUserFollow.setBurst(BurstUtils.encrypt(apUserFollow.getId(), apUserFollow.getUserId()));
            // 保存行为
            FollowBehaviorDto dto = new FollowBehaviorDto();
            dto.setFollowId(followId);
            dto.setArticleId(articleId);
            appFollowBehaviorService.saveFollowBehavior(dto);
            int insert = apUserFollowMapper.insert(apUserFollow);
            return ResponseResult.okResult(insert);
        }else {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST, "该用户已被关注！");
        }
    }

    /**
     *
     * @param user 登录用户id
     * @param followId 被关注人id
     * @return
     */
    private ResponseResult followCancelByUserId(ApUser user, Integer followId) {
        ApUserFollow apUserFollow = apUserFollowMapper.selectByFollowId(BurstUtils.groudOne(user.getId()), user.getId(), followId);
        if(apUserFollow == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"未关注");
        }else {
            ApUserFan apUserFan = apUserFanMapper.selectByFansId(BurstUtils.groudOne(followId), followId, user.getId());
            if(apUserFan != null){
                apUserFanMapper.deleteByFansId(BurstUtils.groudOne(followId), followId, user.getId());
            }
            int count = apUserFollowMapper.deleteByFollowId(BurstUtils.groudOne(user.getId()), user.getId(), followId);
            return ResponseResult.okResult(count);
        }
    }
}
