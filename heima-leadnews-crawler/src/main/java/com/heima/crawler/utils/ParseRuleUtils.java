package com.heima.crawler.utils;

import com.heima.model.crawler.core.parse.ParseRule;
import com.heima.model.crawler.enums.CrawlerEnum;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class ParseRuleUtils {


    /**
     * 获取有效的抓取规则
     *
     * @param html
     * @param parseRuleList
     * @return
     */
    public static List<ParseRule> parseHtmlByRuleList(Html html, List<ParseRule> parseRuleList) {
        List<ParseRule> effectiveGrapRuleList = null;
        if (null != html && null != parseRuleList && !parseRuleList.isEmpty()) {
            //对内容的解析
            List<ParseRule> ruleList = parseContent(html, parseRuleList);
            //对数据有效性的校验
            effectiveGrapRuleList = getEffectiveParseRuleList(ruleList);
        }
        return effectiveGrapRuleList;
    }


    /**
     * 获取有效的抓取规则
     *
     * @return
     */
    private static List<ParseRule> getEffectiveParseRuleList(List<ParseRule> parseRuleList) {
        List<ParseRule> effectiveParseRuleList = new ArrayList<ParseRule>();
        if (null != parseRuleList && !parseRuleList.isEmpty()) {
            for (ParseRule parseRule : parseRuleList) {
                if (parseRule.isAvailability()) {
                    effectiveParseRuleList.add(parseRule);
                }
            }
        }
        return effectiveParseRuleList;
    }

    /**
     * 获取有效的抓取规则
     *
     * @return
     */
    public static boolean validateUrl(List<String> urlList, List<ParseRule> parseRuleList) {
        boolean flag = false;
        if (null != urlList && !urlList.isEmpty()) {
            for (String url : urlList) {
                for (ParseRule parseRule : parseRuleList) {
                    //获取URL校验的正则表达式
                    String validateRegular = parseRule.getUrlValidateRegular();
                    boolean validateResult = true;
                    if (StringUtils.isNotEmpty(validateRegular)) {
                        try {
                            //通过正则表达式进行校验
                            validateResult = Pattern.matches(validateRegular, url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (validateResult) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }
        }
        return flag;
    }


    /**
     * 获取抓取的内容
     *
     * @param html
     * @param parseRuleList
     * @return
     */
    private static List<ParseRule> parseContent(Html html, List<ParseRule> parseRuleList) {
        if (null != html && null != parseRuleList && !parseRuleList.isEmpty()) {
            for (ParseRule parseRule : parseRuleList) {
                List<String> contentList = null;
                //Css表达式的解析
                if (CrawlerEnum.ParseRuleType.CSS == parseRule.getParseRuleType()) {
                    contentList = html.css(parseRule.getRule()).all();
                    //正则表达式的解析
                } else if (CrawlerEnum.ParseRuleType.REGULAR == parseRule.getParseRuleType()) {
                    contentList = html.regex(parseRule.getRule()).all();
                    //Xpath 表达式的解析
                } else if (CrawlerEnum.ParseRuleType.XPATH == parseRule.getParseRuleType()) {
                    contentList = html.xpath(parseRule.getRule()).all();
                }
                if (null != contentList && !contentList.isEmpty()) {
                    parseRule.setParseContentList(contentList);
                }
            }
        }
        return parseRuleList;
    }

    /**
     * 将内容转换为链接地址
     *
     * @param contentList
     * @return
     */
    public static List<String> getUrlLinks(List<String> contentList) {
        List<String> urlList = new ArrayList<String>();
        if (null != contentList && !contentList.isEmpty()) {
            for (String content : contentList) {
                urlList.addAll(Html.create(content).links().all());
            }
        }
        return urlList;
    }

}
