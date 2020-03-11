package com.heima.crawler.process.processor.impl;

import com.heima.crawler.helper.CrawlerHelper;
import com.heima.crawler.process.entity.CrawlerConfigProperty;
import com.heima.crawler.process.processor.AbstractCrawlerPageProcessor;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人空间页
 */
@Component
@Slf4j
public class CrawlerHelpPageProcessor extends AbstractCrawlerPageProcessor {

    @Autowired
    private CrawlerConfigProperty crawlerConfigProperty;

    @Autowired
    private CrawlerHelper crawlerHelper;

    /**
     * 帮助页面的后缀
     */
    private final String helpUrlSuffix = "?utm_source=feed";
    /**
     * 帮助页面分页后缀
     */
    private final String helpPagePagingSuffix = "/article/list/";

    @Override
    public void handelPage(Page page) {
        String handelType = crawlerHelper.getHandelType(page.getRequest());
        long currentTime = System.currentTimeMillis();
        String requestUrl = page.getUrl().get();
        log.info("开始解析帮助页数据，url:{},handelType：{}", requestUrl, handelType);
        // 获取配置的抓取规则
        String helpCrawlerXpath = crawlerConfigProperty.getHelpCrawlerXpath();
        List<String> helpUrlList = page.getHtml().xpath(helpCrawlerXpath).links().all();
        Integer crawlerHelpNextPagingSize = crawlerConfigProperty.getCrawlerHelpNextPagingSize();
        if(crawlerHelpNextPagingSize != null && crawlerHelpNextPagingSize > 1){
            // 分页处理
            List<String> docPagePagingUrlList = getDocPagePagingUrlList(requestUrl, crawlerHelpNextPagingSize);
            if(docPagePagingUrlList != null && !docPagePagingUrlList.isEmpty()){
                helpUrlList.addAll(docPagePagingUrlList);
            }
        }
        addSpiderRequest(helpUrlList, page.getRequest(), CrawlerEnum.DocumentType.PAGE);
        log.info("解析帮助页数据完成，url:{},handelType:{},耗时：{}", page.getUrl(), handelType, System.currentTimeMillis() - currentTime);
    }

    /**
     * 获取分页后的数据
     * @param url 处理的URL
     * @param pageSize 分页页数
     * @return
     */
    private List<String> getDocPagePagingUrlList(String url, Integer pageSize) {
        List<String> docPagePagingUrlList = null;
        if(url.endsWith(helpUrlSuffix)){
            // 获取分页的url 后缀为/list/1的这种
            List<String> pagePagingUrlList = generateHelpPagingUrl(url, pageSize);
            // 获取该页文章的url，即目标url
            docPagePagingUrlList = getHelpPagingDocUrl(pagePagingUrlList);
        }
        return docPagePagingUrlList;
    }

    /**
     * 获取分页的url
     * @param url
     * @param pageSize
     * @return
     */
    private List<String> generateHelpPagingUrl(String url, Integer pageSize) {
        String pageUrl = url.replace(helpUrlSuffix, helpPagePagingSuffix);
        List<String> pagePagingUrlList = new ArrayList<>();
        // 从第二页开始
        for(int i = 2; i < pageSize; i++){
            pagePagingUrlList.add(pageUrl + i);
        }
        return pagePagingUrlList;
    }

    /**
     * 获取文章url
     * @param pagePagingUrlList
     * @return
     */
    private List<String> getHelpPagingDocUrl(List<String> pagePagingUrlList) {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("开始进行分页抓取文章页面");
        List<String> docUrlList = new ArrayList<>();
        int failCount = 0;
        if(!pagePagingUrlList.isEmpty()){
            for (String url : pagePagingUrlList) {
                log.info("开始进行用户文章页面分页处理，url:{}", url);
                // 获取原始html数据
                String htmlData = getOriginalRequestHtmlData(url, null);
                // 检验是否有效
                boolean validate = crawlerHelper.getDataValidateCallBack().validate(htmlData);
                if(validate){
                    // 获取所有的文章url
                    List<String> urlList = new Html(htmlData).xpath(crawlerConfigProperty.getHelpCrawlerXpath()).links().all();
                    if(!urlList.isEmpty()){
                        docUrlList.addAll(urlList);
                    }else {
                        failCount++;
                        if(failCount > 2){
                            break;
                        }
                    }
                }
            }
        }
        log.info("分抓取文章页面完成，耗时:{}", System.currentTimeMillis() - currentTimeMillis);
        return docUrlList;
    }

    @Override
    public boolean isNeedHandelType(String handelType) {
        return CrawlerEnum.HandelType.FORWARD.name().equals(handelType);
    }

    @Override
    public boolean isNeedDocumentType(String documentType) {
        return CrawlerEnum.DocumentType.HELP.name().equals(documentType);
    }

    @Override
    public int getPriority() {
        return 110;
    }
}
