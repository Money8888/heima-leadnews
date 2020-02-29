package com.heima.article.service.impl;

import com.heima.article.service.AppArticleInfoService;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.crawler.core.parse.ZipUtils;
import com.heima.model.mappers.app.ApArticleConfigMapper;
import com.heima.model.mappers.app.ApArticleContentMapper;
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

    Map<String, Object> dataMap = new HashMap<>();

    @Override
    public ResponseResult getArticleInfo(Integer articleId) {

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
}
