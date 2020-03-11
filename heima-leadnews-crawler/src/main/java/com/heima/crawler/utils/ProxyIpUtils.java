package com.heima.crawler.utils;

import com.heima.crawler.factory.CrawlerProxyFactory;
import com.heima.model.crawler.core.proxy.ProxyValidate;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * 代理Ip工具
 */
@Log4j2
public class ProxyIpUtils {
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("crawler");
    private static final String reqUrl = resourceBundle.getString("proxy.validate.url");


    public static void validateProxyIp(ProxyValidate proxyValidate) {
        log.info("开始校验代理IP:" + proxyValidate.getHost() + ":" + proxyValidate.getPort());
        long currentTime = System.currentTimeMillis();
        String errorMessage = null;
        int resultCode = 404;
        try {
            resultCode = processRequest(CrawlerProxyFactory.getHttpHostProxy(proxyValidate.getProxy()));
        } catch (IOException e) {
            errorMessage = e.getMessage();
        }
        int duration = (int) (System.currentTimeMillis() - currentTime) / 1000;
        proxyValidate.setDuration(duration);
        proxyValidate.setReturnCode(resultCode);
        proxyValidate.setError(errorMessage);
        log.info("校验代理IP结束:" + proxyValidate.getHost() + ":" + proxyValidate.getPort() + "  状态码:" + proxyValidate.getReturnCode() + "  耗时" + duration + "秒");
    }


    public static int processRequest(HttpHost proxy) throws IOException {
        Integer statusCode = 404;
        HttpClient client = HttpClientUtils.buildHttpClient(true, null, proxy);
        HttpGet get = HttpClientUtils.buildHttpGet(reqUrl, null, HttpClientUtils.utf8);
        HttpResponse response = client.execute(get);
        statusCode = response.getStatusLine().getStatusCode();


        return statusCode;
    }
}
