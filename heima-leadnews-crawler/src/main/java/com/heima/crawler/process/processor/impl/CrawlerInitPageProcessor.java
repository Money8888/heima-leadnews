package com.heima.crawler.process.processor.impl;

import com.heima.crawler.process.entity.CrawlerConfigProperty;
import com.heima.crawler.process.processor.AbstractCrawlerPageProcessor;
import com.heima.model.crawler.enums.CrawlerEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

import java.util.List;

/**
 * 初始化url
 */
@Component
public class CrawlerInitPageProcessor  extends AbstractCrawlerPageProcessor {

    @Autowired
    private CrawlerConfigProperty crawlerConfigProperty;

    /**
     * 处理用户空间数据
     * @param page
     */
    @Override
    public void handelPage(Page page) {
        String initCrawlerXpath = crawlerConfigProperty.getInitCrawlerXpath();
        List<String> initUrl = page.getHtml().xpath(initCrawlerXpath).links().all();
        // 根据DocumentType 跳转下一个节点
        addSpiderRequest(initUrl,page.getRequest(), CrawlerEnum.DocumentType.HELP);

    }

    /**
     * 需要处理的爬取类型
     * 初始化只处理 正向爬取
     *
     * @param handelType
     * @return
     */
    @Override
    public boolean isNeedHandelType(String handelType) {
        return CrawlerEnum.HandelType.FORWARD.name().equals(handelType);
    }

    /**
     * 需要处理的文档类型
     * 只处理初始化的URL
     * @param documentType
     * @return
     */
    @Override
    public boolean isNeedDocumentType(String documentType) {
        return CrawlerEnum.DocumentType.INIT.name().equals(documentType);
    }

    /**
     * 优先级值越小越优先
     * @return
     */
    @Override
    public int getPriority() {
        return 100;
    }
}
