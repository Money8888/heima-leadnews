package com.heima.crawler.process.entity;

import lombok.Getter;
import lombok.Setter;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 抓取组件
 */
@Setter
@Getter
public class CrawlerComponent implements Serializable {
    /**
     * 页面处理类
     */
    private PageProcessor pageProcessor;
    /**
     * pipelineList 处理
     */
    private List<Pipeline> pipelineList = new ArrayList<Pipeline>();

    /**
     * 去重组件
     */
    private Scheduler scheduler;

    /**
     * 下载组件
     */
    private Downloader downloader;


    public void addPipeline(Pipeline pipeline) {
        pipelineList.add(pipeline);
    }

}
