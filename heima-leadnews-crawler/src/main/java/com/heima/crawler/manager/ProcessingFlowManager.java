package com.heima.crawler.manager;

import com.heima.crawler.config.CrawlerConfig;
import com.heima.crawler.process.ProcessFlow;
import com.heima.crawler.process.entity.CrawlerComponent;
import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/**
 * 前置数据处理
 * 对ProcessFlow 接口类型的类进行前置实例化做一些前置处理
 * 例如AbstractOriginalDataProcess 类的 handel 方式 初始化URL 以及初始化 代理数据
 * 并生成Spider 并自定启动
 * 是爬虫服务的入口
 */
@Component
@Slf4j
public class ProcessingFlowManager {

    @Autowired
    private CrawlerConfig crawlerConfig;

    /**
     * 注入所有的工作流
     */
    @Resource
    private List<ProcessFlow> processFlowList;

    /**
     * spring 启动的时候就会进行调用
     * 对实现ProcessFlow接口的类根据getPriority() 接口对实现类进行从小到大的排序
     * 实现有序的责任链模式 一个模块处理一件事然后将数据传递到下个模块交给下各模块进行处理
     */
    @PostConstruct
    private void initProcessingFlow() {
        if (processFlowList != null && !processFlowList.isEmpty()) {
            processFlowList.sort(new Comparator<ProcessFlow>() {
                @Override
                public int compare(ProcessFlow p1, ProcessFlow p2) {
                    if (p1.getPriority() > p2.getPriority()) {
                        return 1;
                    } else if (p1.getPriority() < p2.getPriority()) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
        Spider spider = configSpider();
        crawlerConfig.setSpider(spider);
    }

    private Spider configSpider() {
        Spider spider = initSpider();
        // 设置线程数
        spider.thread(5);
        return spider;
    }

    /**
     * 根据ProcessFlow接口getComponentType() 接口类型数生成Spider
     * @return
     */
    private Spider initSpider() {
        Spider spider = null;
        CrawlerComponent component = getComponent(processFlowList);
        if(component != null){
            PageProcessor pageProcessor = component.getPageProcessor();
            if(pageProcessor != null){
                spider = Spider.create(pageProcessor);
            }
            if(spider != null){
                if(component.getScheduler() != null){
                    spider.setScheduler(component.getScheduler());
                }
                if(component.getDownloader() != null){
                    spider.setDownloader(component.getDownloader());
                }
                List<Pipeline> pipelineList = component.getPipelineList();
                if(pipelineList != null && !pipelineList.isEmpty()){
                    for (Pipeline pipeline : pipelineList) {
                        spider.addPipeline(pipeline);
                    }
                }
            }
        }
        return spider;
    }

    private CrawlerComponent getComponent(List<ProcessFlow> processFlowList) {
        CrawlerComponent component =new CrawlerComponent();
        for (ProcessFlow processFlow : processFlowList) {
            // 循环每个processor，pipeline，download,scheduler
            if(processFlow.getComponentType() == CrawlerEnum.ComponentType.PAGEPROCESSOR){
                component.setPageProcessor((PageProcessor) processFlow);
            }else if (processFlow.getComponentType() == CrawlerEnum.ComponentType.PIPELINE) {
                component.addPipeline((Pipeline) processFlow);
            } else if (processFlow.getComponentType() == CrawlerEnum.ComponentType.SCHEDULER) {
                component.setScheduler((Scheduler) processFlow);
            } else if (processFlow.getComponentType() == CrawlerEnum.ComponentType.DOWNLOAD) {
                component.setDownloader((Downloader) processFlow);
            }

        }
        return component;
    }

    /**
     * 正向处理
     */
    public void handel() {
        startTask(null, CrawlerEnum.HandelType.FORWARD.name());
    }

    /**
     * 开始处理爬虫任务
     * @param parseItemList
     * @param handelType
     */
    private void startTask(List<ParseItem> parseItemList, String handelType) {
        ProcessFlowData processFlowData = new ProcessFlowData();
        processFlowData.setHandelType(handelType);
        processFlowData.setParseItemList(parseItemList);
        for (ProcessFlow processFlow : processFlowList) {
            processFlow.handel(processFlowData);
        }
        crawlerConfig.getSpider().start();
    }
}
