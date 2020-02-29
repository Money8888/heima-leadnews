package com.heima.article.service.impl;

import com.heima.article.service.AppArticleService;
import com.heima.common.article.constans.ArticleConstans;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.mappers.app.ApArticleMapper;
import com.heima.model.mappers.app.ApUserArticleListMapper;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserArticleList;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@SuppressWarnings("all")
public class AppArticleServiceImpl implements AppArticleService {
    // 每页最多文章大小
    private static final short MAX_PAGE_SIZE = 50;

    @Autowired
    private ApUserArticleListMapper apUserArticleListMapper;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Override
    public ResponseResult load(ArticleHomeDto dto, Short type) {
        // 参数校验
        if(dto == null){
            dto = new ArticleHomeDto();
        }
        // 最大时间处理
        if(dto.getMaxBehotTime()==null){
            dto.setMaxBehotTime(new Date());
        }
        // 最小时间处理
        if(dto.getMinBehotTime()==null){
            dto.setMinBehotTime(new Date());
        }
        // 分页参数校验
        Integer size = dto.getSize();
        if (size == null || size <= 0) {
            size = 20;
        }
        size = Math.min(size, MAX_PAGE_SIZE);
        dto.setSize(size);
        // 文文章频道参数校验
        if(StringUtils.isEmpty(dto.getTag())){
            dto.setTag(ArticleConstans.DEFAULT_TAG);
        }
        // 类型校验
        if(!type.equals(ArticleConstans.LOADTYPE_LOAD_MORE) && !type.equals(ArticleConstans.LOADTYPE_LOAD_NEW)){
            type = ArticleConstans.LOADTYPE_LOAD_MORE;
        }

        // 获取线程中登录用户信息
        ApUser user = AppThreadLocalUtils.getUser();

        if(user != null){
            // 加载根据用户id推荐的文章
            List<ApArticle> apArticleList = getUserArticle(user, dto, type);
            return ResponseResult.okResult(apArticleList);
        }else {
            // 加载默认的文章
            List<ApArticle> apArticleList = getDefaultArticle(dto, type);
            return ResponseResult.okResult(apArticleList);
        }
    }

    private List<ApArticle> getDefaultArticle(ArticleHomeDto dto, Short type) {
        return apArticleMapper.loadArticleListByLocation(dto, type);
    }

    private List<ApArticle> getUserArticle(ApUser user, ArticleHomeDto dto, Short type) {
        List<ApUserArticleList> list = apUserArticleListMapper.loadArticleIdListByUser(user, dto, type);
        if(!list.isEmpty()){
            return apArticleMapper.loadArticleListByIdList(list);
        }else {
            return getDefaultArticle(dto, type);
        }
    }
}
