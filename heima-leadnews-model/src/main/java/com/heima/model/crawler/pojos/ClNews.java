package com.heima.model.crawler.pojos;

import com.heima.model.crawler.core.parse.ZipUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 文章
 */
public class ClNews {
    private Integer id;
    private Integer taskId;
    private String title;
    private String name;
    private int type;
    private Integer channelId;
    private String labels;
    private Date originalTime;
    private Date createdTime;
    private Date submitedTime;
    private Byte status;
    private Date publishTime;
    private String reason;
    private Integer articleId;
    private Integer no;
    private String content;
    public String getUnCompressContent() {
        if (StringUtils.isNotEmpty(content)) {
            return ZipUtils.gunzip(content);
        }
        return content;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public Date getOriginalTime() {
        return originalTime;
    }

    public void setOriginalTime(Date originalTime) {
        this.originalTime = originalTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getSubmitedTime() {
        return submitedTime;
    }

    public void setSubmitedTime(Date submitedTime) {
        this.submitedTime = submitedTime;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}