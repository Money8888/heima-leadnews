package com.heima.crawler.process.parse.impl;

import com.heima.common.common.util.HMStringUtils;
import com.heima.crawler.process.parse.AbstractHtmlParsePipeline;
import com.heima.crawler.process.thread.CrawlerThreadPool;
import com.heima.model.crawler.core.parse.impl.CrawlerParseItem;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Log4j2
public class CrawlerHtmlParsePipeline extends AbstractHtmlParsePipeline<CrawlerParseItem> {


    /**
     * html数据处理入口
     * @param parseItem
     */
    @Override
    public void handelHtmlData(CrawlerParseItem parseItem) {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("将数据加入线程池进行执行，url:{},handelType:{}", parseItem.getUrl(), parseItem.getHandelType());
        CrawlerThreadPool.submit(() -> {
            if(CrawlerEnum.HandelType.FORWARD.name().equals(parseItem.getHandelType())){
                // 正向抓取
                // 添加文章信息
                log.info("开始处理消息,url:{},handelType:{}", parseItem.getUrl(), parseItem.getHandelType());
                addParseItemMessage(parseItem);
            }else if(CrawlerEnum.HandelType.REVERSE.name().equals(parseItem.getHandelType())) {
                // 逆向抓取
                //更新附加数据
                updateAdditional(parseItem);
            }
            log.info("处理文章数据完成，url:{},handelType:{}，耗时：{}", parseItem.getUrl(), parseItem.getHandelType(), System.currentTimeMillis() - currentTimeMillis);
        });
    }


    private void addParseItemMessage(CrawlerParseItem parseItem) {
    }

    private void updateAdditional(CrawlerParseItem parseItem) {
    }

    /**
     * 前置数据处理，将阅读量只保留数字
     * @param itemsAll
     */
    @Override
    public void preParameterHandel(Map<String, Object> itemsAll) {
        String readCount = HMStringUtils.toString(itemsAll.get("readCount"));
        if (StringUtils.isNotEmpty(readCount)) {
            readCount = readCount.split(" ")[1];
            if (StringUtils.isNotEmpty(readCount)) {
                itemsAll.put("readCount", readCount);
            }
        }
    }

    @Override
    public int getPriority() {
        return 1000;
    }
}
