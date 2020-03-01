package com.heima.article.service.impl;

import com.heima.article.service.AppArticleInfoService;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.article.pojos.ApCollection;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApLikesBehavior;
import com.heima.model.behavior.pojos.ApUnlikesBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.crawler.core.parse.ZipUtils;
import com.heima.model.mappers.app.*;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserFollow;
import com.heima.utils.common.BurstUtils;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@SuppressWarnings("all")
public class AppArticleInfoServiceImpl implements AppArticleInfoService {

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;

    @Autowired
    private ApCollectionMapper apCollectionMapper;

    @Autowired
    private ApLikesBehaviorMapper apLikesBehaviorMapper;

    @Autowired
    private ApUnlikesBehaviorMapper apUnlikesBehaviorMapper;

    @Autowired
    private ApAuthorMapper apAuthorMapper;

    @Autowired
    private ApUserFollowMapper apUserFollowMapper;


    @Override
    public ResponseResult getArticleInfo(Integer articleId) {

        Map<String, Object> dataMap = new HashMap<>();

        // 分布式自增id必须大于等于1
        if(articleId == null || articleId < 1){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 读取文章配置信息，判断是否被删除
        ApArticleConfig config = apArticleConfigMapper.selectByArticleId(articleId);

        if(config == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }else if(!config.getIsDelete()){
            ApArticleContent content = apArticleContentMapper.selectByArticleId(articleId);
            String unzipContent = ZipUtils.gunzip(content.getContent());
            content.setContent(unzipContent);
            dataMap.put("content", content);
        }
        dataMap.put("config", config);
        return ResponseResult.okResult(dataMap);
    }

    @Override
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto) {

        Map<String, Object> dataMap = new HashMap<>();

        // 根据用户id或设备id查出行为实体id
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

        // 定义配置映射值
        boolean isUnlike = false, isLike = false, isCollection = false, isFollow = false;
        // 将行为实体id转化为burst值，默认为第一个节点
        String entryBurst = BurstUtils.groudOne(apBehaviorEntry.getId());

        // 查询是否收藏，点赞，不喜欢和关注
        ApCollection apCollection = apCollectionMapper.selectForEntryId(entryBurst, apBehaviorEntry.getId(), dto.getArticleId(), ApCollection.Type.ARTICLE.getCode());
        if(apCollection != null){
            isCollection = true;
        }
        ApLikesBehavior apLikesBehavior = apLikesBehaviorMapper.selectLastLike(entryBurst, apBehaviorEntry.getId(), dto.getArticleId(), ApCollection.Type.ARTICLE.getCode());
        if(apLikesBehavior != null && apLikesBehavior.getOperation() == ApLikesBehavior.Operation.LIKE.getCode()){
            isLike = true;
        }
        ApUnlikesBehavior apUnlikesBehavior = apUnlikesBehaviorMapper.selectLastUnLike(apBehaviorEntry.getId(), dto.getArticleId());
        if(apUnlikesBehavior != null && apUnlikesBehavior.getType() == ApUnlikesBehavior.Type.UNLIKE.getCode()){
            isUnlike = true;
        }

        // 查询是否关注
        ApAuthor apAuthor = apAuthorMapper.selectById(dto.getAuthorId());
        // 将当前登录用户id转化为burst值，默认为第一个节点
        String userBurst = BurstUtils.groudOne(user.getId());
        if(user!=null&&apAuthor!=null&&apAuthor.getUserId()!=null) {
            ApUserFollow apUserFollow = apUserFollowMapper.selectByFollowId(userBurst, user.getId(), apAuthor.getId());
            if(apUserFollow != null){
                isFollow = true;
            }
        }

        dataMap.put("isfollow",isFollow);
        dataMap.put("islike",isLike);
        dataMap.put("isunlike",isUnlike);
        dataMap.put("iscollection",isCollection);
        return ResponseResult.okResult(dataMap);
    }
}
