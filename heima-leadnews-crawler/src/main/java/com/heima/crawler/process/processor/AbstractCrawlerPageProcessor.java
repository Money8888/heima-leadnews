package com.heima.crawler.process.processor;

import com.heima.crawler.helper.CrawlerHelper;
import com.heima.crawler.process.AbstractProcessFlow;
import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.crawler.utils.ParseRuleUtils;
import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.core.parse.ParseRule;
import com.heima.model.crawler.core.parse.impl.CrawlerParseItem;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractCrawlerPageProcessor extends AbstractProcessFlow implements PageProcessor {

    @Autowired
    private CrawlerHelper crawlerHelper;

    @Resource
    private CrawlerPageProcessorManager crawlerPageProcessorManager;

    /**
     * 抽象处理类
     *
     * @param page
     */
    public abstract void handelPage(Page page);

    /**
     * 是否需要处理类型
     *
     * @return
     */
    public abstract boolean isNeedHandelType(String handelType);


    /**
     * 是否需要处理类型
     *
     * @return
     */
    public abstract boolean isNeedDocumentType(String documentType);


    @Override
    public void handel(ProcessFlowData processFlowData) {
    }

    @Override
    public CrawlerEnum.ComponentType getComponentType() {
        return null;
    }

    @Override
    public void process(Page page) {
        long currentTime = System.currentTimeMillis();
        String handelType = crawlerHelper.getHandelType(page.getRequest());
        log.info("开始解析数据页面，url:{},handelType:{}", page.getUrl(), handelType);
        crawlerPageProcessorManager.handel(page);
        log.info("解析数据页面完成，url:{},handelType:{},耗时：{}", page.getUrl(), handelType, System.currentTimeMillis() - currentTime);
    }

    @Override
    public Site getSite() {
        Site site = Site.me()
                .setRetryTimes(getRetryTimes())
                .setRetrySleepTime(getRetrySleepTime())
                .setSleepTime(getSleepTime())
                .setTimeOut(getTimeOut());
        // 配置header
        Map<String, String> headerMap = getHeaderMap();
        if(headerMap != null && !headerMap.isEmpty()){
            for (Map.Entry<String, String> entry: headerMap.entrySet()) {
                site.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return site;
    }

    /**
     * 获取url列表
     *
     * @param helpParseRuleList
     * @return
     */
    public List<String> getHelpUrlList(List<ParseRule> helpParseRuleList){
        List<String> helpUrlList = new ArrayList<String>();
        for (ParseRule parseRule : helpParseRuleList) {
            List<String> urlLinks = ParseRuleUtils.getUrlLinks(parseRule.getParseContentList());
            helpUrlList.addAll(urlLinks);
        }
        return helpUrlList;
    }

    public void addSpiderRequest(List<String> urlList, Request request, CrawlerEnum.DocumentType documentType){
        if(urlList != null && !urlList.isEmpty()){
            List<ParseItem> parseItemList = urlList.stream().map(url -> {
                CrawlerParseItem parseItem = new CrawlerParseItem();
                parseItem.setUrl(url);
                String handelType = crawlerHelper.getHandelType(request);
                parseItem.setDocumentType(documentType.name());
                parseItem.setHandelType(handelType);
                return parseItem;
            }).collect(Collectors.toList());
            addSpiderRequest(parseItemList);
        }
    }

    /**
     * 重试次数
     *
     * @return
     */
    public int getRetryTimes() {
        return 3;
    }

    /**
     * 重试间隔时间 ms
     *
     * @return
     */
    public int getRetrySleepTime() {
        return 1000;
    }

    /**
     * 抓取间隔时间
     *
     * @return
     */
    public int getSleepTime() {
        return 1000;
    }

    /**
     * 超时时间
     *
     * @return
     */
    public int getTimeOut() {
        return 10000;
    }
}
