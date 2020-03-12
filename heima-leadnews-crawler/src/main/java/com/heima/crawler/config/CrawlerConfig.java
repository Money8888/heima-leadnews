package com.heima.crawler.config;

import com.heima.crawler.helper.CookieHelper;
import com.heima.crawler.helper.CrawlerHelper;
import com.heima.crawler.process.entity.CrawlerConfigProperty;
import com.heima.crawler.process.scheduler.DbAndRedisScheduler;
import com.heima.crawler.utils.SeleniumClient;
import com.heima.model.crawler.core.callback.DataValidateCallBack;
import com.heima.model.crawler.core.parse.ParseRule;
import com.heima.model.crawler.core.proxy.CrawlerProxyProvider;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Configuration
@Log4j2
@Getter
@Setter
@PropertySource("classpath:crawler.properties")
@ConfigurationProperties(prefix = "crawler.init.url")
public class CrawlerConfig {

    private String prefix;
    private String suffix;

    private Spider spider;

    @Value("${crux.cookie.name}")
    private static String CRUX_COOKIE_NAME;

    @Value("${crawler.help.nextPagingSize}")
    private static Integer CRAWLER_HELP_NEXTPAGINGSIZE;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("crawler");

    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private int reidsPort;
    @Value("${redis.timeout}")
    private int reidstimeout;

    /**
     * 拼接初始化url
     */
    public List<String> getInitCrawlerUrlList(){
        List<String> initCrawlerUrlList = null;
        if(StringUtils.isNotEmpty(suffix)){
            String[] initCrawlerUrlArray = suffix.split(",");
            if(initCrawlerUrlArray.length > 0){
                for(int i = 0; i < initCrawlerUrlArray.length; i++){
                    String initUrl = initCrawlerUrlArray[i];
                    if(StringUtils.isNotEmpty(initUrl)){
                        if(!initUrl.toLowerCase().startsWith("http")){
                            initUrl = prefix + initUrl;
                            initCrawlerUrlArray[i] = initUrl;
                        }
                    }
                }
            }
            initCrawlerUrlList = Arrays.asList(initCrawlerUrlArray).stream().filter(x -> StringUtils.isNotEmpty(x)).collect(Collectors.toList());
        }
        return initCrawlerUrlList;
    }

    @Bean
    public SeleniumClient getSeleniumClient() {
        return new SeleniumClient();
    }

    /**
     * 设置Cookie辅助类
     *
     * @return
     */
    @Bean
    public CookieHelper getCookieHelper() {
        return new CookieHelper(CRUX_COOKIE_NAME);
    }

    /**
     * 数据校验匿名内部类
     * @param cookieHelper
     * @return
     */
    private DataValidateCallBack getDataValidateCallBack(CookieHelper cookieHelper) {
        return new DataValidateCallBack() {
            @Override
            public boolean validate(String content) {
                boolean flag = true;
                if (StringUtils.isEmpty(content)) {
                    flag = false;
                } else {
                    boolean isContains_acw_sc_v2 = content.contains("acw_sc__v2");
                    boolean isContains_location_reload = content.contains("document.location.reload()");
                    if (isContains_acw_sc_v2 && isContains_location_reload) {
                        flag = false;
                    }
                }
                return flag;
            }
        };
    }



    /**
     * CrawerHelper 辅助类
     *
     * @return
     */
    @Bean
    public CrawlerHelper getCrawerHelper() {
        CookieHelper cookieHelper = getCookieHelper();
        CrawlerHelper crawerHelper = new CrawlerHelper();
        DataValidateCallBack dataValidateCallBack = getDataValidateCallBack(cookieHelper);
        crawerHelper.setDataValidateCallBack(dataValidateCallBack);
        return crawerHelper;
    }

    /**
     * 是否使用代理Ip
     */
    private boolean isUsedProxyIp = Boolean.parseBoolean(resourceBundle.getString("proxy.isUsedProxyIp"));

    /**
     * CrawlerProxyProvider bean
     *
     * @return
     */
    @Bean
    public CrawlerProxyProvider getCrawlerProxyProvider() {
        CrawlerProxyProvider crawlerProxyProvider = new CrawlerProxyProvider();
        crawlerProxyProvider.setUsedProxyIp(isUsedProxyIp);
        return crawlerProxyProvider;
    }

    /**
     * 初始化抓取的Xpath
     */
    private String initCrawlerXpath = "//ul[@class='feedlist_mod']/li[@class='clearfix']/div[@class='list_con']/dl[@class='list_userbar']/dd[@class='name']/a";

    /**
     * 帮助页面抓取Xpath
     */
    private String helpCrawlerXpath = "//div[@class='article-list']/div[@class='article-item-box']/h4/a";

    @Bean
    public CrawlerConfigProperty getCrawlerConfigProperty() {
        CrawlerConfigProperty property = new CrawlerConfigProperty();
        // 初始化url列表
        property.setInitCrawlerUrlList(getInitCrawlerUrlList());
        // 初始化xpath规则定义
        property.setInitCrawlerXpath(initCrawlerXpath);
        // 用户控件下的解析规则
        property.setHelpCrawlerXpath(helpCrawlerXpath);
        // 抓取用户空间下的页大小
        property.setCrawlerHelpNextPagingSize(CRAWLER_HELP_NEXTPAGINGSIZE);
        // 设置目标页解析规则
        property.setTargetParseRuleList(getTargetParseRuleList());
        return property;
    }

    private List<ParseRule> getTargetParseRuleList() {
        List<ParseRule> parseRuleList = new ArrayList<ParseRule>(){{
            //标题
            add(new ParseRule("title", CrawlerEnum.ParseRuleType.XPATH, "//h1[@class='title-article']/text()"));
            //作者
            add(new ParseRule("author", CrawlerEnum.ParseRuleType.XPATH, "//a[@class='follow-nickName']/text()"));
            //发布日期
            add(new ParseRule("releaseDate", CrawlerEnum.ParseRuleType.XPATH, "//span[@class='time']/text()"));
            //标签
            add(new ParseRule("labels", CrawlerEnum.ParseRuleType.XPATH, "//span[@class='tags-box']/a/text()"));
            //个人空间
            add(new ParseRule("personalSpace", CrawlerEnum.ParseRuleType.XPATH, "//a[@class='follow-nickName']/@href"));
            //阅读量
            add(new ParseRule("readCount", CrawlerEnum.ParseRuleType.XPATH, "//span[@class='read-count']/text()"));
            //点赞量
            add(new ParseRule("likes", CrawlerEnum.ParseRuleType.XPATH, "//div[@class='tool-box']/ul[@class='meau-list']/li[@class='btn-like-box']/button/p/text()"));
            //回复次数
            add(new ParseRule("commentCount", CrawlerEnum.ParseRuleType.XPATH, "//div[@class='tool-box']/ul[@class='meau-list']/li[@class='to-commentBox']/button/p/text()"));
            //html内容
            add(new ParseRule("content", CrawlerEnum.ParseRuleType.XPATH, "//div[@id='content_views']/html()"));
        }};
        return parseRuleList;
    }


    @Bean
    public DbAndRedisScheduler getDbAndRedisScheduler() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        JedisPool jedisPool = new JedisPool(genericObjectPoolConfig, redisHost, reidsPort, reidstimeout, null, 0);
        return new DbAndRedisScheduler(jedisPool);
    }
}
