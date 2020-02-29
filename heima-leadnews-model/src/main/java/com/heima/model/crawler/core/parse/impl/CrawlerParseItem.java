package com.heima.model.crawler.core.parse.impl;

import com.heima.model.crawler.core.parse.ParseItem;

public class CrawlerParseItem extends ParseItem {
    /**
     * 数据ID
     */
    private String id;
    /**
     * 说明
     */
    private String summary;
    /**
     * 博客url
     */
    private String url;
    /**
     * 个人空间URL
     */

    private String spatialUrl;

    /**
     * 标签
     */
    private String tag;
    /**
     * 策略
     */
    private String strategy;

    /**
     * 标题
     */
    private String title;

    /**
     * 类型
     */
    private String type;
    /**
     * 文档类型
     */
    private int docType;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 作者
     */
    private String author;

    /**
     * 发布日期
     */
    private String releaseDate;


    /**
     * 阅读量
     */
    private Integer readCount;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 点赞量
     */
    private Integer likes;

    /**
     * 图文内容
     */
    private String content;

    /**
     * 压缩后的内容
     */
    private String compressContent;


    public String getInitialUrl() {
        return getUrl();
    }

    @Override
    public String getParserContent() {
        return getContent();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSpatialUrl() {
        return spatialUrl;
    }

    public void setSpatialUrl(String spatialUrl) {
        this.spatialUrl = spatialUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDocType() {
        return docType;
    }

    public void setDocType(int docType) {
        this.docType = docType;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getCompressContent() {
        return compressContent;
    }

    public void setCompressContent(String compressContent) {
        this.compressContent = compressContent;
    }
}
