package com.heima.model.crawler.core.parse;

import com.heima.model.crawler.enums.CrawlerEnum;

import java.io.Serializable;

/**
 * 解析封装对象
 */
public abstract class ParseItem implements Serializable {
    /**
     * 处理类型 有正向 反向两种
     * FORWARD, 正向 REVERSE 反向
     */
    private CrawlerEnum.HandelType handelType = null;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 获取初始的URL
     *
     * @return
     */
    public abstract String getInitialUrl();

    /**
     * 获取需要处理的内容
     *
     * @return
     */
    public abstract String getParserContent();


    public CrawlerEnum.HandelType getHandelType() {
        return handelType;
    }

    public void setHandelType(CrawlerEnum.HandelType handelType) {
        this.handelType = handelType;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
