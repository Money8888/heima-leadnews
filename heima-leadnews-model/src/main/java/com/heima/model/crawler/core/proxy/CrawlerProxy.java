package com.heima.model.crawler.core.proxy;

import java.io.Serializable;

/**
 * 代理IP实体类
 */
public class CrawlerProxy implements Serializable {


    public CrawlerProxy(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    private String host;

    private Integer port;


    /**
     * 获取代理信息
     *
     * @return
     */
    public String getProxyInfo() {

        return this.host + ":" + port;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "CrawlerProxy{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
