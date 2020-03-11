package com.heima.crawler.utils;

import com.heima.model.crawler.core.cookie.CrawlerCookie;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Request;

import java.util.List;
import java.util.Map;

public class RequestUtils {
    /**
     * request 封装
     *
     * @param url
     * @param headerMap
     * @return
     */
    public static Request requestPackage(String url, Map<String, String> headerMap) {
        Request request = null;
        if (StringUtils.isNotEmpty(url)) {
            request = new Request();
            request.setUrl(url);
            addHeader(request, headerMap);
        }
        return request;
    }


    /**
     * 添加 cookie
     *
     * @param request
     * @param headerMap
     */
    public static void addHeader(Request request, Map<String, String> headerMap) {
        if (null != headerMap && !headerMap.isEmpty()) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }


    /**
     * 添加 cookie
     *
     * @param request
     * @param cookieList
     */
    public static void addCookie(Request request, List<CrawlerCookie> cookieList) {
        if (null != request && null != cookieList && !cookieList.isEmpty()) {
            for (CrawlerCookie cookie : cookieList) {
                if (null != cookie) {
                    request.addCookie(cookie.getName(), cookie.getValue());
                }

            }
        }
    }

}
