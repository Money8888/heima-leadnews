package com.heima.model.crawler.core.cookie;

import java.util.Date;

public class CrawlerCookie {

    public CrawlerCookie() {
    }

    public CrawlerCookie(String name, boolean isRequired) {
        this.name = name;
        this.isRequired = isRequired;
    }

    /**
     * cookie名称
     */
    private String name;
    /**
     * cookie 值
     */
    private String value;
    /**
     * 域名
     */
    private String domain;
    /**
     * 路径
     */
    private String path;


    /**
     * 过期时间
     */
    private Date expire;

    /**
     * 是否是必须的
     */
    private boolean isRequired;

    /**
     * 校验是否过期
     *
     * @return
     */
    public boolean isExpire() {
        boolean flag = false;
        if (null != expire) {
            flag = expire.getTime() <= (new Date()).getTime();
        }
        return flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    @Override
    public String toString() {
        return "CrawlerCookie{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }
}
