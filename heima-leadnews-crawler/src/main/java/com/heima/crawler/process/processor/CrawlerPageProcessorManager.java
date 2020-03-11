package com.heima.crawler.process.processor;

import com.heima.crawler.helper.CrawlerHelper;
import com.heima.crawler.process.ProcessFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/**
 * 爬虫管理类
 * 按照优先级决定顺序和实现handel方法
 */
@Component
public class CrawlerPageProcessorManager {

    @Autowired
    private CrawlerHelper crawlerHelper;

    @Resource
    private List<AbstractCrawlerPageProcessor> abstractCrawlerPageProcessorList;

    /**
     * 初始化注入spring容器的接口排序
     */
    @PostConstruct
    private void initProcessingFlow(){
        if(abstractCrawlerPageProcessorList != null &&!abstractCrawlerPageProcessorList.isEmpty()){
            // 按照优先级排序,采用匿名内部类
            abstractCrawlerPageProcessorList.sort(new Comparator<ProcessFlow>() {
                @Override
                public int compare(ProcessFlow o1, ProcessFlow o2) {
                    if(o1.getPriority() > o2.getPriority()){
                        return 1;
                    }else if(o1.getPriority() < o2.getPriority()){
                        return -1;
                    }
                    return 0;
                }
            });
        }
    }

    public void handel(Page page){
        String handelType = crawlerHelper.getHandelType(page.getRequest());
        String documentType = crawlerHelper.getDocumentType(page.getRequest());
        for (AbstractCrawlerPageProcessor abstractCrawlerPageProcessor : abstractCrawlerPageProcessorList) {
            boolean isNeedHandelType = abstractCrawlerPageProcessor.isNeedHandelType(handelType);
            boolean isNeedDocumentType = abstractCrawlerPageProcessor.isNeedDocumentType(documentType);
            if(isNeedDocumentType && isNeedHandelType){
                abstractCrawlerPageProcessor.handelPage(page);
            }
        }
    }
}
