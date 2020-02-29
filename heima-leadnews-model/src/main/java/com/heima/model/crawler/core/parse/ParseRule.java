package com.heima.model.crawler.core.parse;

import com.heima.model.crawler.enums.CrawlerEnum;

import java.util.List;

/**
 * 抓取内容封装
 */
public class ParseRule {
    /**
     * 映射字段
     */
    private String field;
    /**
     * URL 校验规则
     */
    private String urlValidateRegular;

    /**
     * 解析规则类型
     */
    private CrawlerEnum.ParseRuleType parseRuleType;
    /**
     * 规则
     */
    private String rule;

    /**
     * 抓取内容列表
     */
    private List<String> parseContentList;

    public ParseRule() {
    }

    /**
     * 构造方法
     * @param field
     * @param parseRuleType
     * @param rule
     */
    public ParseRule(String field, CrawlerEnum.ParseRuleType parseRuleType, String rule) {
        this.field = field;
        this.parseRuleType = parseRuleType;
        this.rule = rule;
    }

    /**
     * 检查是否有效，如果内容为空则判断该类为空
     * @return
     */
    public boolean isAvailability() {
        boolean isAvailability = false;
        if (null != parseContentList && !parseContentList.isEmpty()) {
            isAvailability = true;
        }
        return isAvailability;
    }

    /**
     * 获取合并后的内容
     *
     * @return
     */
    public String getMergeContent() {
        StringBuilder stringBuilder = new StringBuilder();
        if (null != parseContentList && !parseContentList.isEmpty()) {
            for (String str : parseContentList) {
                stringBuilder.append(str);
            }
        }
        return stringBuilder.toString();
    }


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getUrlValidateRegular() {
        return urlValidateRegular;
    }

    public void setUrlValidateRegular(String urlValidateRegular) {
        this.urlValidateRegular = urlValidateRegular;
    }

    public CrawlerEnum.ParseRuleType getParseRuleType() {
        return parseRuleType;
    }

    public void setParseRuleType(CrawlerEnum.ParseRuleType parseRuleType) {
        this.parseRuleType = parseRuleType;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public List<String> getParseContentList() {
        return parseContentList;
    }

    public void setParseContentList(List<String> parseContentList) {
        this.parseContentList = parseContentList;
    }
}
