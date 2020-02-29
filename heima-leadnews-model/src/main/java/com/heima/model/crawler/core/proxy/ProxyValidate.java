package com.heima.model.crawler.core.proxy;

/**
 * proxy 校验
 */
public class ProxyValidate {

    public ProxyValidate() {
    }

    public ProxyValidate(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 主机
     */
    private String host;
    /**
     * 端口号
     */
    private int port;
    /**
     * 错误码
     */
    private int returnCode;

    /**
     * 耗时
     */
    private int duration;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 获取代理对象
     * @return
     */
    public CrawlerProxy getProxy() {
        return new CrawlerProxy(host, port);
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ProxyIpValidate{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", returnCode=" + returnCode +
                ", duration=" + duration +
                ", error='" + error + '\'' +
                '}';
    }
}
