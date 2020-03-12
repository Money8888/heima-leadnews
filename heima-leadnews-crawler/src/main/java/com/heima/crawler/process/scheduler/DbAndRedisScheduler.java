package com.heima.crawler.process.scheduler;

import com.heima.crawler.helper.CrawlerHelper;
import com.heima.crawler.process.ProcessFlow;
import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.crawler.service.CrawlerNewsAdditionalService;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * redis和数据库排重
 * 防止爬取的url重复
 */
@Log4j2
public class DbAndRedisScheduler  extends RedisScheduler implements ProcessFlow {

    @Autowired
    private CrawlerHelper crawlerHelper;

    @Autowired
    private CrawlerNewsAdditionalService crawlerNewsAdditionalService;

    public DbAndRedisScheduler(String host){
        super(host);
    }

    public DbAndRedisScheduler(JedisPool pool){
        super(pool);
    }

    /**
     * 判断是否重复
     * @param request
     * @param task
     * @return
     */
    @Override
    public boolean isDuplicate(Request request, Task task) {
        String handelType = crawlerHelper.getHandelType(request);
        boolean isExists = false;
        // 正向爬虫
        if(CrawlerEnum.HandelType.FORWARD.name().equals(handelType)){
            log.info("URL排重开始，URL:{},documentType:{}", request.getUrl(), handelType);
            isExists = super.isDuplicate(request, task);
            if(!isExists){
                isExists = crawlerNewsAdditionalService.isExistsUrl(request.getUrl());
            }
            log.info("URL排重结束，URL:{}，handelType:{},isExists：{}", request.getUrl(), handelType, isExists);
        }else {
            log.info("反向抓取，不进行URL排重");
        }
        return isExists;
    }

    @Override
    public void handel(ProcessFlowData processFlowData) {

    }

    @Override
    public CrawlerEnum.ComponentType getComponentType() {
        return CrawlerEnum.ComponentType.SCHEDULER;
    }

    @Override
    public int getPriority() {
        return 123;
    }
}
