package com.heima.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heima.admin.service.ReviewMediaArticleService;
import com.heima.common.aliyun.AliyunImageScanRequest;
import com.heima.common.aliyun.AliyunTextScanRequest;
import com.heima.common.common.contants.ESIndexConstants;
import com.heima.common.common.pojo.EsIndexEntity;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.crawler.core.parse.ZipUtils;
import com.heima.model.mappers.admin.AdChannelMapper;
import com.heima.model.mappers.app.*;
import com.heima.model.mappers.wemedia.WmNewsMapper;
import com.heima.model.mappers.wemedia.WmUserMapper;
import com.heima.model.media.pojos.WmNews;
import com.heima.model.media.pojos.WmUser;
import com.heima.model.user.pojos.ApUserMessage;
import com.heima.utils.common.Compute;
import com.heima.utils.common.SimHashUtils;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 自动审核文章
 * 文章状态
 * 0 草稿 1 提交（待审核） 2 审核失败 3人工审核 4 已审核 8 审核通过（待发布） 9 已发布
 */
@Service
@SuppressWarnings("all")
@Slf4j
public class ReviewMediaArticleServiceImpl implements ReviewMediaArticleService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Autowired
    private ApAuthorMapper apAuthorMapper;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private AdChannelMapper adChannelMapper;

    @Autowired
    private ApUserMessageMapper apUserMessageMapper;

    @Autowired
    private JestClient jestClient;

    @Autowired
    private AliyunTextScanRequest aliyunTextScanRequest;

    @Autowired
    private AliyunImageScanRequest aliyunImageScanRequest;

    @Value("{FILE_SERVER_URL}")
    private String fileServerUrl;

    private double review_article_pass;

    private double review_article_review;

    @Override
    public void autoReviewArticleByMedia(Integer newsId) {
        // 根据文章id查询文章内容
        WmNews wmNews = wmNewsMapper.selectNewsDetailByPrimaryKey(newsId);
        if(wmNews != null){
            // 若文章已审核，直接保存
            if(wmNews.getStatus() == 4){
                reviewSuccessSaveAll(wmNews);
                return;
            }
            // 若文章状态为8，且发布时间大于当前时间
            if(wmNews.getStatus() == 8 && wmNews.getPublishTime() != null && wmNews.getPublishTime().getTime() < new Date().getTime()){
                reviewSuccessSaveAll(wmNews);
            }
            // 若文章状态为1
            if(wmNews.getStatus() == 1){
                // 根据文章标题匹配文章内容匹配度
                String content = wmNews.getContent();
                String title = wmNews.getTitle();
                // 调用海明距离进行文本字符串的距离衡量，越小越相似
//                double semblance = SimHashUtils.getSemblance(title, content, 64);
//                if(semblance<review_article_pass && semblance>review_article_review){
//                    //人工审核，修改状态
//                    updateWmNews(wmNews, (short) 3, "人工复审");
//                    return;
//                }
//                if(semblance<review_article_review){
//                    //文章与标题不匹配，拒绝
//                    updateWmNews(wmNews, (short) 2, "文章与标题不匹配");
//                    return;
//                }
                double degree = Compute.SimilarDegree(content, title);
                if(degree<=0){
                    //文章标题与内容匹配
                    updateWmNews(wmNews,(short)2,"文章标题与内容不匹配");
                    return;
                }
                // 调用阿里接口，审核文本
                List<String> imageList = new ArrayList<>();
                StringBuilder sb = new StringBuilder();
                JSONArray jsonArray = JSON.parseArray(content);
                handlerTextAndImages(imageList, sb, jsonArray);
                try {
                    String textResponse = aliyunTextScanRequest.textScanRequest(sb.toString());
                    if("review".equals(textResponse)){
                        updateWmNews(wmNews, (short)3, "需人工审核");
                        return;
                    }
                    if("block".equals(textResponse)){
                        updateWmNews(wmNews, (short)2,"文本审核不通过");
                        return;
                    }
                    // 调用阿里接口，审核图片
                    String imageResponse = aliyunImageScanRequest.imageScanRequest(imageList);
                    if(imageResponse == null){
                        if("review".equals(imageResponse)){
                            updateWmNews(wmNews, (short)3, "需人工审核");
                            return;
                        }
                        if("block".equals(imageResponse)){
                            updateWmNews(wmNews, (short)2,"图片审核不通过");
                            return;
                        }
                    }else {
                        updateWmNews(wmNews, (short)2,"图片访问失败");
                        return;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                if(wmNews.getPublishTime() != null){
                    if(wmNews.getPublishTime().getTime() > new Date().getTime()){
                        // 更改文章状态为8
                        updateWmNews(wmNews, (short) 8, "待发布");
                    }else {
                        reviewSuccessSaveAll(wmNews);
                    }
                }else {
                    reviewSuccessSaveAll(wmNews);
                }

            }
        }
    }

    /**
     * 处理content  找出文本和图片列表
     *
     * @param images
     * @param sb
     * @param jsonArray
     */
    private void handlerTextAndImages(List<String> images, StringBuilder sb, JSONArray jsonArray) {
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            String  type = (String) jsonObject.get("type");
            if("image".equals(type)){
                images.add((String) jsonObject.get("value"));
            }
            if("text".equals(type)){
                sb.append(jsonObject.get("value"));
            }
        }
    }
    /**
     * 修改文章状态信息和原因
     * @param wmNews
     * @param status
     * @param message
     */
    private void updateWmNews(WmNews wmNews, short status, String message) {
        wmNews.setStatus(status);
        wmNews.setReason(message);
        wmNewsMapper.updateByPrimaryKeySelective(wmNews);
    }

    /**
     * 审核通过保存文章全部信息
     * 存入数据   ap_article_config   ap_article   ap_article_content  ap_author
     * @param wmNews
     */
    private void reviewSuccessSaveAll(WmNews wmNews) {
        ApAuthor apAuthor = null;
        // 保存ap_author表
        if(wmNews.getUserId() != null){
            WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
            if(wmUser != null && wmUser.getName() != null){
                apAuthor = apAuthorMapper.selectByAuthorName(wmUser.getName());
                if(apAuthor == null || apAuthor.getId() == null){
                    apAuthor = new ApAuthor();
                    apAuthor.setUserId(wmNews.getUserId());
                    apAuthor.setCreatedTime(new Date());
                    apAuthor.setType(2);
                    apAuthor.setName(wmUser.getName());
                    apAuthor.setWmUserId(wmUser.getId());
                    apAuthorMapper.insert(apAuthor);
                }
            }
        }

        // 保存 ap_article表
        ApArticle apArticle = new ApArticle();
        if(apAuthor != null){
            apArticle.setAuthorId(apAuthor.getId().longValue());
            apArticle.setAuthorName(apAuthor.getName());
        }
        apArticle.setCreatedTime(new Date());
        Integer channelId = wmNews.getChannelId();
        if(channelId != null){
            AdChannel adChannel = adChannelMapper.selectByPrimaryKey(channelId);
            apArticle.setChannelId(channelId);
            apArticle.setChannelName(adChannel.getName());
        }
        apArticle.setLayout(wmNews.getType());
        apArticle.setTitle(wmNews.getTitle());
        String images = wmNews.getImages();
        if(images != null){
            String[] split = images.split(",");
            StringBuilder sb = new StringBuilder();
            for(int i = 0;i < split.length; i++){
                if(i > 0){
                    sb.append(",");
                }
                sb.append(fileServerUrl);
                sb.append(split[i]);
            }
            apArticle.setImages(sb.toString());
        }
        apArticleMapper.insert(apArticle);

        // ap_article_content
        ApArticleContent apArticleContent = new ApArticleContent();
        apArticleContent.setArticleId(apArticle.getId());
        apArticleContent.setContent(ZipUtils.gzip(wmNews.getContent()));
        apArticleContentMapper.insert(apArticleContent);

        // ap_article_config
        ApArticleConfig apArticleConfig = new ApArticleConfig();
        apArticleConfig.setArticleId(apArticle.getId());
        apArticleConfig.setIsComment(true);
        apArticleConfig.setIsDelete(false);
        apArticleConfig.setIsDown(false);
        apArticleConfig.setIsForward(true);
        apArticleConfigMapper.insert(apArticleConfig);

        // 创建es索引
        EsIndexEntity esIndexEntity = new EsIndexEntity();
        esIndexEntity.setId(apArticle.getId().longValue());
        // 防止出现空指针异常
        esIndexEntity.setChannelId(new Long(channelId));
        esIndexEntity.setContent(wmNews.getContent());
        esIndexEntity.setPublishTime(new Date());
        esIndexEntity.setStatus(new Long(1));
        esIndexEntity.setTitle(wmNews.getTitle());
        if(wmNews.getUserId() != null){
            esIndexEntity.setUserId(wmNews.getUserId());
        }
        esIndexEntity.setTag("media");
        Index.Builder builder = new Index.Builder(esIndexEntity);
        builder.id(apArticle.getId().toString());
        builder.refresh(true);
        Index build = builder.index(ESIndexConstants.ARTICLE_INDEX)
                .type(ESIndexConstants.DEFAULT_DOC)
                .build();
        JestResult result = null;
        try {
            result = jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("执行ES创建索引失败，message:{}",e.getMessage());
        }

        if(result == null || !result.isSucceeded()){
            log.error("插入更新索引失败：message:{}",result.getErrorMessage());
        }

        // 修改wmNews状态
        wmNews.setArticleId(apArticle.getId());
        updateWmNews(wmNews,(short)9, "审核成功");
        // 通知用户审核成功
        ApUserMessage apUserMessage = new ApUserMessage();
        apUserMessage.setUserId(wmNews.getUserId());
        apUserMessage.setCreatedTime(new Date());
        apUserMessage.setIsRead(false);
        apUserMessage.setContent("文章审核成功");
        apUserMessage.setType(108);// 文章审核通过
        apUserMessageMapper.insertSelective(apUserMessage);


    }
}
