package com.heima.crawler.proxy;

import com.heima.common.common.util.HMStringUtils;
import com.heima.crawler.service.CrawlerIpPoolService;
import com.heima.crawler.utils.ProxyIpUtils;
import com.heima.crawler.utils.SeleniumClient;
import com.heima.model.crawler.core.cookie.CrawlerHtml;
import com.heima.model.crawler.core.proxy.CrawlerProxyProvider;
import com.heima.model.crawler.core.proxy.ProxyValidate;
import com.heima.model.crawler.pojos.ClIpPool;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Log4j2
public class ProxyIpManager {

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("crawler");
    //获取代理IP配置的URL
    private static final String proxyGetUrl = resourceBundle.getString("proxy.get.url");
    /**
     * 抓取IP的正则表达式 预编译模式
     */
    Pattern proxyIpParttern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)");

    @Autowired
    private CrawlerProxyProvider crawlerProxyProvider;

    @Autowired
    private CrawlerIpPoolService crawlerIpPoolService;

    @Autowired
    private SeleniumClient seleniumClient;

    /**
     * 校验动态代理IP
     */
    public void validateProxyIp() {
        List<ClIpPool> clIpPoolList = crawlerIpPoolService.queryList(new ClIpPool());
        if (null != clIpPoolList && !clIpPoolList.isEmpty()) {
            for (ClIpPool clIpPool : clIpPoolList) {
                boolean odlEnable = clIpPool.getEnable();
                //如果状态是可用
                validateProxyIp(clIpPool);
                //如果原始状态以及当前状态都是不可用，则判断是废弃的代理，删除
                if (!odlEnable && !clIpPool.getEnable()) {
                    crawlerIpPoolService.delete(clIpPool);
                    log.info("删除代理IP" + clIpPool.getIp() + ":" + clIpPool.getPort());
                } else {
                    crawlerIpPoolService.updateCrawlerIpPool(clIpPool);
                    log.info("更新代理IP" + clIpPool.getIp() + ":" + clIpPool.getPort());
                }
            }
        }
    }

    /**
     * 更新动态代理IP
     */
    public void updateProxyIp() {
        List<ClIpPool> clIpPoolList = getGrabClIpPoolList();
        if (null != clIpPoolList && !clIpPoolList.isEmpty()) {
            for (ClIpPool clIpPool : clIpPoolList) {
                validateProxyIp(clIpPool);
                if (clIpPool.getEnable()) {
                    boolean isExis = crawlerIpPoolService.checkExist(clIpPool.getIp(), clIpPool.getPort());
                    if (!isExis) {
                        crawlerIpPoolService.saveCrawlerIpPool(clIpPool);
                        log.info("插入代理IP:" + clIpPool.getIp() + ":" + clIpPool.getPort());
                    }
                }
            }
        }
    }

    /**
     * 抓取获取的动态代理IP
     *
     * @return
     */
    private List<ClIpPool> getGrabClIpPoolList() {
        List<ClIpPool> clIpPoolList = new ArrayList<ClIpPool>();
        //使用SeleniumUtils的方式获取代理IP数据
        CrawlerHtml crawlerHtml = seleniumClient.getCrawlerHtml(proxyGetUrl, crawlerProxyProvider.getRandomProxy(), "yd_cookie");
        if (null != crawlerHtml && StringUtils.isNotEmpty(crawlerHtml.getHtml())) {
            //通过正则表达式来获取代理IP数据
            Matcher matcher = proxyIpParttern.matcher(crawlerHtml.getHtml());
            while (matcher.find()) {
                String host = matcher.group(1);
                String port = matcher.group(2);
                ClIpPool clIpPool = new ClIpPool();
                clIpPool.setSupplier("89免费代理");
                clIpPool.setIp(host);
                clIpPool.setCreatedTime(new Date());
                clIpPool.setPort(Integer.parseInt(port));
                clIpPoolList.add(clIpPool);
            }
        }
        return clIpPoolList;
    }

    /**
     * 校验IP是否可用
     *
     * @param clIpPool
     */
    private void validateProxyIp(ClIpPool clIpPool) {
        clIpPool.setEnable(false);
        ProxyValidate proxyValidate = new ProxyValidate(clIpPool.getIp(), clIpPool.getPort());
        try {
            ProxyIpUtils.validateProxyIp(proxyValidate);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (proxyValidate.getReturnCode() == 200) {
            clIpPool.setEnable(true);
        }
        clIpPool.setCode(proxyValidate.getReturnCode());
        clIpPool.setDuration(proxyValidate.getDuration());
        clIpPool.setError(HMStringUtils.getFixedLengthStr(proxyValidate.getError(), 70));
    }
}
