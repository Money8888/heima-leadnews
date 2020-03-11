package com.heima.crawler.process.original.impl;

import com.heima.crawler.config.CrawlerConfig;
import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.crawler.process.original.AbstractOriginalDataProcess;
import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.core.parse.impl.CrawlerParseItem;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
public class CsdnOriginalDataProcess extends AbstractOriginalDataProcess {

    @Autowired
    private CrawlerConfig crawlerConfig;

    @Override
    public List<ParseItem> parseOriginalRequestData(ProcessFlowData processFlowData) {
        List<ParseItem> parseItemList = null;
        List<String> initCrawlerUrlList = crawlerConfig.getInitCrawlerUrlList();
        if(!initCrawlerUrlList.isEmpty() && initCrawlerUrlList != null){
            parseItemList = initCrawlerUrlList.stream().map(url -> {
                CrawlerParseItem parseItem = new CrawlerParseItem();
                // url加上时间戳
                url = url + "?rnd=" + System.currentTimeMillis();
                parseItem.setUrl(url);
                parseItem.setDocumentType(CrawlerEnum.DocumentType.INIT.name());
                parseItem.setHandelType(processFlowData.getHandelType());
                log.info("初始化URL:{}", url);
                return parseItem;
            }).collect(Collectors.toList());
        }
        return parseItemList;
    }

    @Override
    public int getPriority() {
        return 10;
    }
}
