package com.heima.crawler.helper;

import com.heima.crawler.factory.CrawlerProxyFactory;
import com.heima.crawler.utils.SeleniumClient;
import com.heima.model.crawler.core.callback.ConcurrentCallBack;
import com.heima.model.crawler.core.cookie.CrawlerCookie;
import com.heima.model.crawler.core.cookie.CrawlerHtml;
import com.heima.model.crawler.core.delayed.DelayedUtils;
import com.heima.model.crawler.core.proxy.CrawlerProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CookieHelper 用于管理爬取过程中使用对应代理Cookie的管理，
 * CSDN网站的关键cookie与IP地址做了绑定，
 * 如果用非本机的IP访问就会被拦截所以要管理各种代理的cookie,
 * 并且在cookie失效后进行自动更新。
 */
public class CookieHelper {


    /**
     * 代理IP存放的地方
     * <ip+端口号,cookie列表>
     */
    private Map<String, List<CrawlerCookie>> proxyCookieCacheMap = new ConcurrentHashMap<String, List<CrawlerCookie>>();
    /**
     * 数据锁
     */
    private final String SYNCHRONIZED_TAG = "SYNCHRONIZED_TAG";

    private SeleniumClient seleniumClient = new SeleniumClient();

    public CookieHelper() {
    }


    /**
     * 关键获取cookie的名称
     */
    private String cookieName;


    public CookieHelper(String cookieName) {
        this.cookieName = cookieName;
    }


    /**
     * 并发过滤器 用于多个并发进行访问的时候只有一个并发进行操作，其他并发被拦截
     */
    private final ConcurrentCallBack concurrentFilter = DelayedUtils.getConcurrentFilter(50000);

    /**
     * 强制更新Cookie
     *
     * @param url
     * @return
     */
    public List<CrawlerCookie> updateCookie(String url, CrawlerProxy proxy) {
        List<CrawlerCookie> cookieList = getProxyCookieList(proxy);
        if (null != cookieList) {
            cookieList.clear();
            List<CrawlerCookie> tmpList = getCookieEntity(url, proxy);
            updateCookie(tmpList, proxy);
        }
        return cookieList;
    }

    /**
     * 更新Cookie
     *
     * @param crawlerCookieList
     */
    public void updateCookie(List<CrawlerCookie> crawlerCookieList, CrawlerProxy proxy) {
        if (null != crawlerCookieList && !crawlerCookieList.isEmpty()) {
            putProxyCookieList(proxy, crawlerCookieList);
        }
    }


    /**
     * 获取Cookie
     *
     * @param url
     * @return
     */
    public List<CrawlerCookie> getCookieEntity(String url, CrawlerProxy proxy) {
        CrawlerCookie crawlerCookie = getCookieEntity(url, getCookieName(), proxy);
        return new ArrayList<CrawlerCookie>() {{
            add(crawlerCookie);
        }};
    }

    /**
     * 获取缓存的Cookie列表
     *
     * @param url
     * @param proxy
     * @return
     */
    public List<CrawlerCookie> getCacheCookieList(String url, CrawlerProxy proxy) {
        List<CrawlerCookie> cookieList = getProxyCookieList(proxy);
        if (null != cookieList && !cookieList.isEmpty()) {
            return cookieList;
        } else {
            List<CrawlerCookie> tmpList = getAloneCookieEntity(url, cookieName, proxy);
            updateCookie(tmpList, proxy);
            return cookieList;
        }
    }


    /**
     * 获取Cookie
     *
     * @param url
     * @return
     */
    public CrawlerCookie getCookieEntity(String url, String cookieName, CrawlerProxy proxy) {
        CrawlerCookie resultCookie = null;
        List<CrawlerCookie> crawlerCookieList = getCacheCookieList(url, proxy);
        if (null != crawlerCookieList && !crawlerCookieList.isEmpty()) {
            for (CrawlerCookie crawlerCookie : crawlerCookieList) {
                if (crawlerCookie.getName().equals(cookieName)) {
                    resultCookie = crawlerCookie;
                }
            }
        }
        return resultCookie;
    }

    /**
     * 单独获取Cookie
     *
     * @param url
     * @return
     */
    private List<CrawlerCookie> getAloneCookieEntity(String url, String cookieName, CrawlerProxy proxy) {
        synchronized (SYNCHRONIZED_TAG) {
            List<CrawlerCookie> crawlerCookieList = null;
            boolean filter = concurrentFilter.filter();
            if (filter) {
                CrawlerHtml crawlerHtml = seleniumClient.getCrawlerHtml(url, proxy, cookieName);
                if (null != crawlerHtml) {
                    crawlerCookieList = crawlerHtml.getCrawlerCookieList();
                }

            }

            return crawlerCookieList;
        }
    }


    /**
     * 获取代理Cookie
     *
     * @param crawlerProxy
     * @return
     */
    private List<CrawlerCookie> getProxyCookieList(CrawlerProxy crawlerProxy) {
        String proxyInfo = CrawlerProxyFactory.getCrawlerProxyInfo(crawlerProxy);
        List<CrawlerCookie> cookieList = proxyCookieCacheMap.get(proxyInfo);
        if (null == cookieList) {
            cookieList = new ArrayList<CrawlerCookie>();
            putProxyCookieList(crawlerProxy, cookieList);
        }
        return cookieList;
    }


    /**
     * 添加代理Cookie
     *
     * @param crawlerProxy
     * @param cookieList
     */
    private void putProxyCookieList(CrawlerProxy crawlerProxy, List<CrawlerCookie> cookieList) {
        String proxyInfo = CrawlerProxyFactory.getCrawlerProxyInfo(crawlerProxy);
        proxyCookieCacheMap.remove(proxyInfo);
        proxyCookieCacheMap.put(proxyInfo, cookieList);
    }


    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCookieName() {
        return cookieName;
    }

}
