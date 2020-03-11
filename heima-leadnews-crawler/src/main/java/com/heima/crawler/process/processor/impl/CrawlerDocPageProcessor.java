package com.heima.crawler.process.processor.impl;

import com.heima.crawler.helper.CrawlerHelper;
import com.heima.crawler.process.entity.CrawlerConfigProperty;
import com.heima.crawler.process.processor.AbstractCrawlerPageProcessor;
import com.heima.crawler.utils.ParseRuleUtils;
import com.heima.model.crawler.core.parse.ParseRule;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

import java.util.List;

@Component
@Slf4j
public class CrawlerDocPageProcessor  extends AbstractCrawlerPageProcessor {

    @Autowired
    private CrawlerConfigProperty crawlerConfigProperty;

    @Autowired
    private CrawlerHelper crawlerHelper;

    @Override
    public void handelPage(Page page) {
        long currentTimeMillis = System.currentTimeMillis();
        String handelType = crawlerHelper.getHandelType(page.getRequest());
        log.info("开始解析目标页数据，url:{},handelType:{}", page.getUrl(), handelType);
        // 获取文章内容
        List<ParseRule> targetParseRuleList = crawlerConfigProperty.getTargetParseRuleList();
        // 抽取有效数据
        targetParseRuleList = ParseRuleUtils.parseHtmlByRuleList(page.getHtml(), targetParseRuleList);
        if(targetParseRuleList != null && !targetParseRuleList.isEmpty()){
            for (ParseRule parseRule : targetParseRuleList) {
                // 将数据以键值对添加至page，以便pipeline处理
                log.info("添加数据字段到field，url:{}，handelType:{},field:{}", page.getUrl(), handelType, parseRule.getField());
                page.putField(parseRule.getField(), parseRule.getMergeContent());
            }
        }
        log.info("解析目标页数据完成，url:{},handelType:{},耗时：{}", page.getUrl(), handelType, System.currentTimeMillis() - currentTimeMillis);
    }

    @Override
    public boolean isNeedHandelType(String handelType) {
        return CrawlerEnum.HandelType.FORWARD.equals(handelType);
    }

    @Override
    public boolean isNeedDocumentType(String documentType) {
        return CrawlerEnum.DocumentType.PAGE.name().equals(documentType);
    }

    @Override
    public int getPriority() {
        return 120;
    }
}
