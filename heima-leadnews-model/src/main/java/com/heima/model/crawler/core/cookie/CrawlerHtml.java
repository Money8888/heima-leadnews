package com.heima.model.crawler.core.cookie;

import com.heima.model.crawler.core.proxy.CrawlerProxy;

import java.util.List;

public class CrawlerHtml {


    private String html;

    private String url;

    private CrawlerProxy proxy;

    private List<CrawlerCookie> crawlerCookieList = null;

    public CrawlerHtml(String url) {
    }


    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<CrawlerCookie> getCrawlerCookieList() {
        return crawlerCookieList;
    }


    public void setCrawlerCookieList(List<CrawlerCookie> crawlerCookieList) {
        this.crawlerCookieList = crawlerCookieList;
    }

    public CrawlerProxy getProxy() {
        return proxy;
    }

    public void setProxy(CrawlerProxy proxy) {
        this.proxy = proxy;
    }

    public String  getUrl() {
        return url;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
