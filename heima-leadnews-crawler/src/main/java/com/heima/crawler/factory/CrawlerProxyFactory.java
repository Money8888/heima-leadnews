package com.heima.crawler.factory;

import com.heima.model.crawler.core.proxy.CrawlerProxy;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

/**
 * 代理工厂
 */
public class CrawlerProxyFactory {

    /**
     * 不使用代理
     */
    private static final String NOT_USE_PROXY = "NOT_USE_PROXY";


    /**
     * 代理对象httpclient的代理
     * @param crawlerProxy
     * @return
     */
    public static HttpHost getHttpHostProxy(CrawlerProxy crawlerProxy) {
        if (null != crawlerProxy && StringUtils.isNotEmpty(crawlerProxy.getHost()) && null != crawlerProxy.getPort()) {
            return new HttpHost(crawlerProxy.getHost(), crawlerProxy.getPort());
        }
        return null;
    }

    /**
     * 获取webmagic 代理对象
     *
     * @return
     */
    public static us.codecraft.webmagic.proxy.Proxy getWebmagicProxy(CrawlerProxy crawlerProxy) {
        if (null != crawlerProxy && StringUtils.isNotEmpty(crawlerProxy.getHost()) && null != crawlerProxy.getPort()) {
            return new us.codecraft.webmagic.proxy.Proxy(crawlerProxy.getHost(), crawlerProxy.getPort());
        }
        return null;
    }

    /**
     * 获取selenium Cookie
     *
     * @return
     */
    public static org.openqa.selenium.Proxy getSeleniumProxy(CrawlerProxy crawlerProxy) {
        if (null != crawlerProxy && StringUtils.isNotEmpty(crawlerProxy.getHost()) && null != crawlerProxy.getPort()) {
            org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
            proxy.setHttpProxy(crawlerProxy.getProxyInfo());
            return proxy;
        }
        return null;
    }

    /**
     * 获取ProxyInfo 信息
     *
     * @param proxy
     * @return
     */
    public static String getCrawlerProxyInfo(CrawlerProxy proxy) {
        String proxyInfo = NOT_USE_PROXY;
        if (null != proxy) {
            proxyInfo = proxy.getProxyInfo();
        }
        return proxyInfo;
    }
}
