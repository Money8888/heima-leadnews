package com.heima.model.crawler.core.proxy;

import com.heima.model.crawler.core.callback.ProxyProviderCallBack;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 代理IP的提供者
 */
public class CrawlerProxyProvider {
    /**
     * 读写锁特点
     * 读读共享
     * 写写互斥
     * 读写互斥
     */
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    //获取读锁
    private Lock readLock = lock.readLock();
    //获取写锁
    private Lock writeLock = lock.writeLock();
    /**
     * 随机数生成器，用以随机获取代理IP
     */
    private Random random = new Random();

    private boolean isUsedProxyIp = true;

    public CrawlerProxyProvider() {
    }

    public CrawlerProxyProvider(List<CrawlerProxy> crawlerProxyList) {
        this.crawlerProxyList = crawlerProxyList;
    }

    /**
     * 代理Ip池
     */
    private List<CrawlerProxy> crawlerProxyList = null;
    /**
     * ip池回调
     */
    private ProxyProviderCallBack proxyProviderCallBack;
    
    /**
     * 随机获取一个代理IP保证每次请求使用的IP都不一样
     *
     * @return
     */
    public CrawlerProxy getRandomProxy() {
        CrawlerProxy crawlerProxy = null;
        readLock.lock();
        try {
            if (isUsedProxyIp && null != crawlerProxyList && !crawlerProxyList.isEmpty()) {
                int randomIndex = random.nextInt(crawlerProxyList.size());
                crawlerProxy = crawlerProxyList.get(randomIndex);
            }
        } finally {
            readLock.unlock();
        }

        return crawlerProxy;
    }

    public void updateProxy() {
        //不使用代理IP 则不进行更新
        if (!isUsedProxyIp) {
            return;
        }
        if (null != proxyProviderCallBack) {
            writeLock.lock();
            try {
                crawlerProxyList = proxyProviderCallBack.getProxyList();
            } finally {
                writeLock.unlock();
            }
        }
    }


    public List<CrawlerProxy> getCrawlerProxyList() {
        return crawlerProxyList;
    }

    public void setCrawlerProxyList(List<CrawlerProxy> crawlerProxyList) {
        this.crawlerProxyList = crawlerProxyList;
    }

    public boolean isUsedProxyIp() {
        return isUsedProxyIp;
    }

    public void setUsedProxyIp(boolean usedProxyIp) {
        isUsedProxyIp = usedProxyIp;
    }

    public ProxyProviderCallBack getProxyProviderCallBack() {
        return proxyProviderCallBack;
    }

    public void setProxyProviderCallBack(ProxyProviderCallBack proxyProviderCallBack) {
        this.proxyProviderCallBack = proxyProviderCallBack;
    }
}
