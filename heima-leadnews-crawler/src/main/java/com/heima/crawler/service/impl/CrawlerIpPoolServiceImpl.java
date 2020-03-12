package com.heima.crawler.service.impl;

import com.heima.crawler.service.CrawlerIpPoolService;
import com.heima.model.crawler.core.proxy.CrawlerProxy;
import com.heima.model.crawler.pojos.ClIpPool;
import com.heima.model.mappers.crawerls.ClIpPoolMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("all")
public class CrawlerIpPoolServiceImpl  implements CrawlerIpPoolService {

    @Autowired
    private ClIpPoolMapper clIpPoolMapper;


    @Override
    public void saveCrawlerIpPool(ClIpPool clIpPool) {
        clIpPoolMapper.insertSelective(clIpPool);
    }

    @Override
    public boolean checkExist(String host, int port) {
        ClIpPool clIpPool = new ClIpPool();
        clIpPool.setIp(host);
        clIpPool.setPort(port);
        List<ClIpPool> clIpPoolList = clIpPoolMapper.selectList(clIpPool);
        if(clIpPoolList != null && !clIpPoolList.isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public void updateCrawlerIpPool(ClIpPool clIpPool) {
        clIpPoolMapper.updateByPrimaryKey(clIpPool);
    }

    @Override
    public List<ClIpPool> queryList(ClIpPool clIpPool) {
        return clIpPoolMapper.selectList(clIpPool);
    }

    @Override
    public List<ClIpPool> queryAvailableList(ClIpPool clIpPool) {
        return clIpPoolMapper.selectAvailableList(clIpPool);
    }

    @Override
    public void delete(ClIpPool clIpPool) {
        clIpPoolMapper.deleteByPrimaryKey(clIpPool.getId());
    }

    @Override
    public void unvailableProxy(CrawlerProxy proxy, String errorMsg) {
        ClIpPool clIpPoolQuery = new ClIpPool();
        clIpPoolQuery.setIp(proxy.getHost());
        clIpPoolQuery.setPort(proxy.getPort());
        clIpPoolQuery.setIsEnable(true);
        List<ClIpPool> clIpPoolList = clIpPoolMapper.selectList(clIpPoolQuery);
        if(clIpPoolList != null && !clIpPoolList.isEmpty()){
            for (ClIpPool clIpPool : clIpPoolList) {
                clIpPool.setIsEnable(false);
                clIpPool.setError(errorMsg);
                clIpPoolMapper.updateByPrimaryKey(clIpPool);
            }
        }
    }
}
