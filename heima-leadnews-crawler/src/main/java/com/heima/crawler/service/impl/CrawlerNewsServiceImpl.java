package com.heima.crawler.service.impl;

import com.heima.crawler.service.CrawlerNewsService;
import com.heima.model.crawler.pojos.ClNews;
import com.heima.model.mappers.crawerls.ClNewsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("all")
public class CrawlerNewsServiceImpl implements CrawlerNewsService {
    @Autowired
    private ClNewsMapper clNewsMapper;
    @Override
    public void saveNews(ClNews clNews) {
        clNewsMapper.insertSelective(clNews);
    }
    @Override
    public void deleteByUrl(String url) {
        clNewsMapper.deleteByUrl(url);
    }
    @Override
    public List<ClNews> queryList(ClNews clNews) {
        return clNewsMapper.selectList(clNews);
    }
    @Override
    public void updateNews(ClNews clNews) {
        clNewsMapper.updateByPrimaryKey(clNews);
    }
}