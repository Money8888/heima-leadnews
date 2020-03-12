package com.heima.crawler.process;


import com.alibaba.fastjson.JSON;
import com.heima.crawler.config.CrawlerConfig;
import com.heima.crawler.factory.CrawlerProxyFactory;
import com.heima.crawler.helper.CookieHelper;
import com.heima.crawler.helper.CrawlerHelper;
import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.crawler.utils.HttpClientUtils;
import com.heima.crawler.utils.JsonValidator;
import com.heima.crawler.utils.RequestUtils;
import com.heima.crawler.utils.SeleniumClient;
import com.heima.model.crawler.core.cookie.CrawlerCookie;
import com.heima.model.crawler.core.cookie.CrawlerHtml;
import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.core.parse.impl.CrawlerParseItem;
import com.heima.model.crawler.core.proxy.CrawlerProxy;
import com.heima.model.crawler.core.proxy.CrawlerProxyProvider;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Request;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * ProcessFlow 的抽象模板类，对其他子类通用方法的一些抽取
 * 已经模板抽象方法的一些定义
 */
@Log4j2
public abstract class AbstractProcessFlow implements ProcessFlow {

    @Autowired
    private CrawlerConfig crawlerConfig;

    @Autowired
    private CrawlerHelper crawlerHelper;

    @Autowired
    private CookieHelper cookieHelper;

    @Autowired
    private SeleniumClient seleniumClient;

    @Autowired
    private CrawlerProxyProvider crawlerProxyProvider;

    /**
     * UA
     * user agent 意思是用户代理。用户代理是一种对数据打包、创造分组头，以及编址、传递消息的部件。
     * 用户代理是指浏览器,它的信息包括硬件平台、系统软件、应用软件和用户个人偏好.用户代理，它还包括搜索引擎。
     */
    private final String UserAgent[] = {
            "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
            "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729; InfoPath.3; rv:11.0) like Gecko",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36 OPR/37.0.2178.32",
            "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 UBrowser/5.6.12150.8 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36 OPR/37.0.2178.32",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36"
    };

    /**
     * 设置 Accept
     * <p>
     * Accept 请求头用来告知客户端可以处理的内容类型，这种内容类型用MIME类型来表示。借助内容协商机制, 服务器可以从诸多备选项中选择一项进行应用，
     * 并使用 Content-Type 应答头通知客户端它的选择。浏览器会基于请求的上下文来为这个请求头设置合适的值，比如获取一个CSS层叠样式表时值与获取图片、视频或脚本文件时的值是不同的。
     */
    private final String Accept[] = {
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3"
    };


    /**
     * UserAgent 参数设置
     */
    public final String UserAgentParameterName = "User-Agent";


    /**
     * UserAgent 参数设置
     */
    public final String AcceptParameterName = "Accept";

    /**
     * 获取header头
     *
     * @return
     */
    public Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put(UserAgentParameterName, getUserAgent());
        headerMap.put(AcceptParameterName, getAccept());
        return headerMap;
    }

    /**
     * request 封装
     *
     * @param url
     * @return
     */
    public Request getRequest(String url) {
        Map<String, String> headerMap = getHeaderMap();
        Request request = RequestUtils.requestPackage(url, headerMap);
        return request;
    }

    /**
     * request 封装
     *
     * @param parseItem
     * @return
     */
    public Request getRequest(ParseItem parseItem) {
        Request request = null;
        String initialUrl = parseItem.getInitialUrl();
        if (StringUtils.isNotEmpty(initialUrl)) {
            request = getRequest(initialUrl);
            crawlerHelper.setParseItem(request, parseItem);
        }
        return request;
    }

    /**
     * 添加request
     *
     * @param parseItemList
     */
    public void addSpiderRequest(List<ParseItem> parseItemList) {
        if (null != parseItemList && !parseItemList.isEmpty()) {
            for (ParseItem parseItem : parseItemList) {
                Request request = getRequest(parseItem);
                crawlerConfig.getSpider().addRequest(request);
            }
        }
    }


    /**
     * 获取随机UA
     */
    public String getUserAgent() {
        return UserAgent[(int) (Math.random() * (UserAgent.length))];
    }

    /**
     * 获取随机Accept
     */
    public String getAccept() {
        return Accept[(int) (Math.random() * (Accept.length))];
    }

    /**
     * 获取原始的Html 页面数据
     *
     * @param url
     * @param parameterMap
     * @return
     */
    public String getOriginalRequestHtmlData(String url, Map<String, String> parameterMap) {
        //获取代理
        CrawlerProxy proxy = crawlerProxyProvider.getRandomProxy();

        //获取Cookie列表
        List<CrawlerCookie> cookieList = cookieHelper.getCookieEntity(url, proxy);
        //通过HttpClient方式来获取数据
        String htmlData = getHttpClientRequestData(url, parameterMap, cookieList, proxy);
        boolean isValidate = crawlerHelper.getDataValidateCallBack().validate(htmlData);
        if (!isValidate) {
            CrawlerHtml crawlerHtml = getSeleniumRequestData(url, parameterMap, proxy);
            htmlData = crawlerHtml.getHtml();
        }
        return htmlData;
    }


    /**
     * 通过Http Client 来获取数据
     *
     * @param url          请求的URL
     * @param parameterMap 参数
     * @param cookieList   cookie列表
     * @param crawlerProxy 代理
     * @return
     */
    public String getHttpClientRequestData(String url, Map<String, String> parameterMap, List<CrawlerCookie> cookieList, CrawlerProxy crawlerProxy) {
        CookieStore cookieStore = getCookieStore(cookieList);
        String jsonDate = null;
        HttpHost proxy = null;
        if (null != crawlerProxy) {
            proxy = CrawlerProxyFactory.getHttpHostProxy(crawlerProxy);
        }
        try {
            long currentTime = System.currentTimeMillis();
            log.info("HttpClient 请求数据,url:{},parameter:{},cookies:{},proxy:{}", url, parameterMap, JSON.toJSONString(cookieList), proxy);
            jsonDate = HttpClientUtils.get(url, parameterMap, getHeaderMap(), cookieStore, proxy, "UTF-8");
            log.info("HttpClient 请求数据完成：url:{},parameter:{},cookies:{},proxy:{},duration:{},result:{}", url, parameterMap, JSON.toJSONString(cookieList), proxy, System.currentTimeMillis() - currentTime, jsonDate);
        } catch (IOException e) {
            log.error("HttpClient 请求数据异常,url:{},parameter:{},cookies:{},proxy:{},errorMsg:{}", url, parameterMap, JSON.toJSONString(cookieList), proxy, e.getMessage());
        } catch (URISyntaxException e) {
            log.error("HttpClient 请求数据异常,url:{},parameter:{},cookies:{},proxy:{},errorMsg:{}", url, parameterMap, JSON.toJSONString(cookieList), proxy, e.getMessage());
        }
        return jsonDate;
    }


    /**
     * 获取 SeleniumRequestData
     *
     * @param url
     * @param parameterMap
     * @return
     */
    public CrawlerHtml getSeleniumRequestData(String url, Map<String, String> parameterMap, CrawlerProxy proxy) {
        String buildUrl = HttpClientUtils.buildGetUrl(url, parameterMap, HttpClientUtils.utf8);
        String cookieName = cookieHelper.getCookieName();
        CrawlerHtml crawlerHtml = seleniumClient.getCrawlerHtml(buildUrl, proxy, cookieName);
        if (null != crawlerHtml) {
            cookieHelper.updateCookie(crawlerHtml.getCrawlerCookieList(), proxy);
        }
        return crawlerHtml;
    }


    /**
     * cookie 转 CookieStore
     *
     * @param cookieList
     * @return
     */
    private CookieStore getCookieStore(List<CrawlerCookie> cookieList) {
        BasicCookieStore cookieStore = null;
        if (null != cookieList && !cookieList.isEmpty()) {
            for (CrawlerCookie cookie : cookieList) {
                if (null != cookie) {
                    BasicClientCookie basicClientCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
                    basicClientCookie.setDomain(cookie.getDomain());
                    basicClientCookie.setPath(cookie.getPath());
                    cookieStore = new BasicCookieStore();
                    cookieStore.addCookie(basicClientCookie);
                }
            }
        }
        return cookieStore;
    }

    /**
     * 获取原始的请求的JSON数据
     *
     * @param url
     * @param parameterMap
     * @return
     */
    public String getOriginalRequestJsonData(String url, Map<String, String> parameterMap) {
        //获取代理
        CrawlerProxy proxy = crawlerProxyProvider.getRandomProxy();

        //获取Cookie列表
        List<CrawlerCookie> cookieList = cookieHelper.getCookieEntity(url, proxy);
        //通过HttpClient方式来获取数据
        String jsonData = getHttpClientRequestData(url, parameterMap, cookieList, proxy);
        //如果不是JSON 说明数据抓取失败则通过SeleniumUtils的方式来获取数据
        if (!isJson(jsonData)) {
            CrawlerHtml crawlerHtml = getSeleniumRequestData(url, parameterMap, proxy);
            jsonData = seleniumClient.getJsonData(crawlerHtml);
        }
        return jsonData;
    }


    /**
     * 验证 字符串是否是json格式
     *
     * @param jsonData
     * @return
     */
    public boolean isJson(String jsonData) {
        boolean isJson = false;
        try {
            isJson = JsonValidator.getJsonValidator().validate(jsonData);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return isJson;
    }
}
