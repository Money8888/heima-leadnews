package com.heima.crawler.process.parse.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heima.common.common.util.HMStringUtils;
import com.heima.common.kafka.KafkaSender;
import com.heima.common.kafka.messages.SubmitArticleAuthMessage;
import com.heima.crawler.process.parse.AbstractHtmlParsePipeline;
import com.heima.crawler.process.thread.CrawlerThreadPool;
import com.heima.crawler.service.AdLabelService;
import com.heima.crawler.service.CrawlerNewsAdditionalService;
import com.heima.crawler.service.CrawlerNewsCommentService;
import com.heima.crawler.service.CrawlerNewsService;
import com.heima.crawler.utils.DateUtils;
import com.heima.crawler.utils.HtmlParser;
import com.heima.model.crawler.core.label.HtmlLabel;
import com.heima.model.crawler.core.parse.ZipUtils;
import com.heima.model.crawler.core.parse.impl.CrawlerParseItem;
import com.heima.model.crawler.enums.CrawlerEnum;
import com.heima.model.crawler.pojos.ClNews;
import com.heima.model.crawler.pojos.ClNewsAdditional;
import com.heima.model.crawler.pojos.ClNewsComment;
import com.heima.model.mess.admin.SubmitArticleAuto;
import com.heima.utils.common.ReflectUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Log4j2
public class CrawlerHtmlParsePipeline extends AbstractHtmlParsePipeline<CrawlerParseItem> {


    @Autowired
    private CrawlerNewsAdditionalService crawlerNewsAdditionalService;

    @Autowired
    private CrawlerNewsService crawlerNewsService;

    @Autowired
    private AdLabelService adLabelService;

    @Autowired
    private CrawlerNewsCommentService crawlerNewsCommentService;

    @Autowired
    private KafkaSender kafkaSender;

    @Value("${crawler.nextupdatehours}")
    private String nextUpdateHours;

//    @Value("${csdn.comment.url}")
//    private String csdnCommentUrl;

    // 因为字符串中含有$符号

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("crawler");
    private static final String csdnCommentUrl = resourceBundle.getString("csdn.comment.url");
    /**
     * html数据处理入口
     * @param parseItem
     */
    @Override
    public void handelHtmlData(CrawlerParseItem parseItem) {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("将数据加入线程池进行执行，url:{},handelType:{}", parseItem.getUrl(), parseItem.getHandelType());
        CrawlerThreadPool.submit(() -> {
            if(CrawlerEnum.HandelType.FORWARD.name().equals(parseItem.getHandelType())){
                // 正向抓取
                // 添加文章信息
                log.info("开始处理消息,url:{},handelType:{}", parseItem.getUrl(), parseItem.getHandelType());
                addParseItemMessage(parseItem);
            }else if(CrawlerEnum.HandelType.REVERSE.name().equals(parseItem.getHandelType())) {
                // 逆向抓取
                //更新附加数据
                updateAdditional(parseItem);
            }
            log.info("处理文章数据完成，url:{},handelType:{}，耗时：{}", parseItem.getUrl(), parseItem.getHandelType(), System.currentTimeMillis() - currentTimeMillis);
        });
    }


    /**
     * 添加解析后的数据
     * @param parseItem
     */
    private void addParseItemMessage(CrawlerParseItem parseItem) {
        long currentTimeMillis = System.currentTimeMillis();
        String url = null;
        String handelType = null;
        if(parseItem != null){
            url = parseItem.getUrl();
            handelType = parseItem.getHandelType();
            log.info("开始添加数据,url:{},handelType:{}", url, parseItem.getHandelType());
            // 添加文章数据
            ClNews clNews = addClNewsData(parseItem);
            if(clNews != null){
                // 添加附加数据
                addAdditional(parseItem, clNews);
                // 添加评论回复数据
                if(parseItem.getCommentCount() != null && parseItem.getCommentCount() > 0){
                    addCommentData(parseItem, clNews);
                }
                sendSubmitArticleAutoMessage(clNews.getId());
            }
        }
        log.info("添加数据完成,url:{},handelType:{},耗时:{}", url, handelType, System.currentTimeMillis() - currentTimeMillis);
    }


    /**
     * 发送自动审核
     * @param clNewId
     */
    private void sendSubmitArticleAutoMessage(Integer clNewId) {
        log.info("开始发送自动审核消息,id:{}", clNewId);
        SubmitArticleAuto submitArticleAuto = new SubmitArticleAuto();
        submitArticleAuto.setArticleId(clNewId);
        submitArticleAuto.setType(SubmitArticleAuto.ArticleType.CRAWLER);
        SubmitArticleAuthMessage submitArticleAuthMessage = new SubmitArticleAuthMessage();
        submitArticleAuthMessage.setData(submitArticleAuto);
        kafkaSender.sendSubmitArticleAuthMessage(submitArticleAuthMessage);
        log.info("发送自动审核消息完成,id:{}", clNewId);
    }

    /**
     * 添加评论数据
     * @param parseItem
     * @param clNews
     */
    private void addCommentData(CrawlerParseItem parseItem, ClNews clNews) {
        long currentTime = System.currentTimeMillis();
        log.info("开始获取文章评论数据");
        List<ClNewsComment> commentList = getCommentData(parseItem);
        if(commentList != null && !commentList.isEmpty()){
            for (ClNewsComment clNewsComment : commentList) {
                clNewsComment.setNewsId(clNews.getId());
                crawlerNewsCommentService.saveClNewsComment(clNewsComment);
            }
        }
        log.info("获取文章评论数据完成，耗时：{}", System.currentTimeMillis() - currentTime);
    }

    /**
     * 获取评论列表
     * @param parseItem
     * @return
     */
    private List<ClNewsComment> getCommentData(CrawlerParseItem parseItem) {
        // 构建评论的url
        String buildCommentUrl = buildCommentUrl(parseItem);
        // 调用父类httpclient发送请求获取数据
        String jsonData = getOriginalRequestJsonData(buildCommentUrl, null);
        // 解析获取的json数据
        List<ClNewsComment> commentList = analysisCommentJsonData(jsonData);
        return commentList;
    }

    /**
     * 解析评论的json数据
     * @param jsonData
     * @return
     */
    private List<ClNewsComment> analysisCommentJsonData(String jsonData) {
        if(StringUtils.isEmpty(jsonData)){
            return null;
        }
        List<ClNewsComment> commentList = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Map<String, Object> map = jsonObject.getObject("data", Map.class);
        JSONArray jsonArray = (JSONArray) map.get("list");
        if(jsonArray != null){
            List<Map> dataInfoList = jsonArray.toJavaList(Map.class);
            for (Map<String, Object> dataInfo : dataInfoList) {
                JSONObject infoObject = (JSONObject) dataInfo.get("info");
                Map infoMap = infoObject.toJavaObject(Map.class);
                ClNewsComment comment = new ClNewsComment();
                comment.setContent(HMStringUtils.toString(infoMap.get("Content")));
                comment.setUsername(HMStringUtils.toString(infoMap.get("UserName")));
                Date date = DateUtils.stringToDate(HMStringUtils.toString(infoMap.get("PostTime")), DateUtils.DATE_TIME_FORMAT);
                comment.setCommentDate(date);
                comment.setCreatedDate(new Date());
                commentList.add(comment);
            }
        }
        return commentList;
    }

    /**
     * 拼接访问请求的评论url
     * @param parseItem
     * @return
     */
    private String buildCommentUrl(CrawlerParseItem parseItem) {
        String buildCommentUrl = csdnCommentUrl;
        Map<String, Object> map = ReflectUtils.beanToMap(parseItem);
        for(Map.Entry<String, Object> entry : map.entrySet()){
            String key = entry.getKey();
            String buildKey = "${" + key + "}";
            Object value = entry.getValue();
            if(value != null){
                String strValue = value.toString();
                buildCommentUrl = buildCommentUrl.replace(buildKey, strValue);
            }
        }
        return buildCommentUrl;
    }

    /**
     * 处理文章附加信息
     *
     * @param parseItem
     * @param clNews
     */
    private void addAdditional(CrawlerParseItem parseItem, ClNews clNews) {
        long currentTime = System.currentTimeMillis();
        log.info("开始处理文章附加数据");
        if (null != parseItem && null != clNews) {
            ClNewsAdditional clNewsAdditional = toClNewsAdditional(parseItem, clNews);
            crawlerNewsAdditionalService.saveAdditional(clNewsAdditional);
        }
        log.info("文章附加数据处理完成,耗时：{}", System.currentTimeMillis() - currentTime);
    }

    /**
     * 转换为文章附加信息
     *
     * @param parseItem
     * @param clNews
     * @return
     */
    private ClNewsAdditional toClNewsAdditional(CrawlerParseItem parseItem, ClNews clNews) {
        ClNewsAdditional clNewsAdditional = null;
        if (null != parseItem) {
            clNewsAdditional = new ClNewsAdditional();
            //设置文章ID
            clNewsAdditional.setNewsId(clNews.getId());
            //设置阅读数
            clNewsAdditional.setReadCount(parseItem.getReadCount());
            //设置回复数
            clNewsAdditional.setComment(parseItem.getCommentCount());
            //设置点赞数
            clNewsAdditional.setLikes(parseItem.getLikes());
            //设置URL
            clNewsAdditional.setUrl(parseItem.getUrl());
            //设置更新时间
            clNewsAdditional.setUpdatedTime(new Date());
            //设置创建时间
            clNewsAdditional.setCreatedTime(new Date());
            //设置更新次数
            clNewsAdditional.setUpdateNum(0);
            //设置下次更新时间
            int nextUpdateHour = getNextUpdateHours(clNewsAdditional.getUpdateNum());
            /**
             * 设置下次更新时间
             */
            clNewsAdditional.setNextUpdateTime(DateUtils.addHours(new Date(), nextUpdateHour));
        }
        return clNewsAdditional;
    }

    /**
     * 获取下次更新时间
     *
     * @param updateNum
     * @return
     */
    private int getNextUpdateHours(int updateNum) {
        if(StringUtils.isNotEmpty(nextUpdateHours)){
            String[] updateArray = nextUpdateHours.split(",");
            return Integer.parseInt(updateArray[updateNum]);
        }else{
            // 设置默认值
            // 表示2的多少次方
            return 2 << updateNum;
        }
    }

    /**
     * 添加文章内容
     * @param parseItem
     * @return
     */
    private ClNews addClNewsData(CrawlerParseItem parseItem) {
        log.info("开始添加文章内容");
        ClNews clNews = new ClNews();
        if(parseItem != null){
            //将Html内容转换为HtmlLabel 对象类别
            HtmlParser htmlParser = HtmlParser.getHtmlParser(getParseExpression(), getDefHtmlStyleMap());
            List<HtmlLabel> htmlLabelList = htmlParser.parseHtml(parseItem.getContent());
            // 获取文章类型
            int type = getDocType(htmlLabelList);
            parseItem.setDocType(type);
            // 内容转化为json
            String jsonStr = JSON.toJSONString(htmlLabelList);
            // 压缩
            parseItem.setCompressContent(ZipUtils.gzip(jsonStr));
            // 判重
            ClNewsAdditional clNewsAdditional = crawlerNewsAdditionalService.getAdditionalByUrl(parseItem.getUrl());
            if(clNewsAdditional != null){
                // 转化为clnews
                clNews = toClNews(parseItem);
                long currentTime = System.currentTimeMillis();
                log.info("开始插入新的文章");
                // 保存新文章
                crawlerNewsService.saveNews(clNews);
                log.info("插入新的文章完成，耗时：{}", System.currentTimeMillis() - currentTime);
            }else {
                log.info("文章URL已存在不重复添加，URL：{}", clNewsAdditional.getUrl());
            }
        }
        log.info("添加文章内容完成");
        return clNews;
    }

    /**
     * 转化成ClNews对象
     * @param parseItem
     * @return
     */
    private ClNews toClNews(CrawlerParseItem parseItem) {
        ClNews clNews = new ClNews();
        clNews.setName(parseItem.getAuthor());
        clNews.setLabels(parseItem.getLabels());
        clNews.setContent(parseItem.getCompressContent());
        clNews.setLabelIds(adLabelService.getLabelIds(parseItem.getLabels()));
        Integer channelId = adLabelService.getAdChannelByLabelIds(clNews.getLabelIds());
        clNews.setChannelId(channelId);
        clNews.setTitle(parseItem.getTitle());
        clNews.setType(parseItem.getDocType());
        clNews.setStatus((byte)1);
        clNews.setCreatedTime(new Date());
        String releaseDate = parseItem.getReleaseDate();
        if(StringUtils.isNotEmpty(releaseDate)){
            clNews.setOriginalTime(DateUtils.stringToDate(releaseDate, DateUtils.DATE_TIME_FORMAT_CHINESE));
        }
        return clNews;
    }

    /**
     * 获取图文类型
     * 0：无图片
     * 1：单图
     * 2：多图
     * @param htmlLabelList
     * @return
     */
    private int getDocType(List<HtmlLabel> htmlLabelList) {
        int type = 0;
        int num = 0;
        if(htmlLabelList != null && !htmlLabelList.isEmpty()){
            for (HtmlLabel htmlLabel : htmlLabelList) {
                if(CrawlerEnum.HtmlType.IMG_TAG.getDataType().equals(htmlLabel.getType())){
                    num ++;
                }
            }
        }
        if (num == 0) {
            type = 0;
        } else if (num == 1) {
            type = 1;
        } else {
            type = 2;
        }
        return type;
    }

    /**
     * 反向操作更新
     * @param parseItem
     */
    private void updateAdditional(CrawlerParseItem parseItem) {
        long currentTime = System.currentTimeMillis();
        log.info("开始更新文章附加数据");
        if(parseItem != null){
            ClNewsAdditional clNewsAdditional = crawlerNewsAdditionalService.getAdditionalByUrl(parseItem.getUrl());
            if(clNewsAdditional != null){
                // 将id和url设置不变
                clNewsAdditional.setNewsId(null);
                clNewsAdditional.setUrl(null);
                //阅读量设置
                clNewsAdditional.setReadCount(parseItem.getReadCount());
                //评论数设置
                clNewsAdditional.setComment(parseItem.getCommentCount());
                //点赞数设置
                clNewsAdditional.setLikes(parseItem.getLikes());
                //更新时间
                clNewsAdditional.setUpdatedTime(new Date());
                // 更新次数
                clNewsAdditional.setUpdateNum(clNewsAdditional.getUpdateNum() + 1);
                int nextUpdateHours = getNextUpdateHours(clNewsAdditional.getUpdateNum());
                clNewsAdditional.setNextUpdateTime(DateUtils.addHours(new Date(), nextUpdateHours));
                crawlerNewsAdditionalService.updateAdditional(clNewsAdditional);
            }
        }
        log.info("更新文章附加数据完成，耗时：{}", System.currentTimeMillis() - currentTime);
    }

    /**
     * 前置数据处理，将阅读量只保留数字
     * @param itemsAll
     */
    @Override
    public void preParameterHandel(Map<String, Object> itemsAll) {
        String readCount = HMStringUtils.toString(itemsAll.get("readCount"));
        if (StringUtils.isNotEmpty(readCount)) {
            readCount = readCount.split(" ")[1];
            if (StringUtils.isNotEmpty(readCount)) {
                itemsAll.put("readCount", readCount);
            }
        }
    }

    @Override
    public int getPriority() {
        return 1000;
    }
}
