package com.heima.model.crawler.core.request;

import com.heima.model.crawler.enums.CrawlerEnum;

import java.io.Serializable;
import java.util.List;

public class RequestData implements Serializable {

    public RequestData() {
    }

    public RequestData(String url, String category, CrawlerEnum.ReturnDataType returnDataType) {
        this.url = url;
        this.category = category;
        this.returnDataType = returnDataType;
    }

    /**
     * 访问的URL
     */
    private String url;

    /**
     * 类别
     */
    private String category;
    /**
     * 返回数据类型
     */
    private CrawlerEnum.ReturnDataType returnDataType;

    /**
     * 返回数据结果集
     */
    private List<String> resultList;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public CrawlerEnum.ReturnDataType getReturnDataType() {
        return returnDataType;
    }

    public void setReturnDataType(CrawlerEnum.ReturnDataType returnDataType) {
        this.returnDataType = returnDataType;
    }

    public List<String> getResultList() {
        return resultList;
    }

    public void setResultList(List<String> resultList) {
        this.resultList = resultList;
    }
}
