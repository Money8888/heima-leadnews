package com.heima.model.crawler.core.cookie;

import java.util.List;

public class CrawlerHtml {


    private String html;

    private List<CrawlerCookie> crawlerCookieList = null;


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
}
