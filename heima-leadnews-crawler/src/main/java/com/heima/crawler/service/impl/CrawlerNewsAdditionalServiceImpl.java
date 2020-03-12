package com.heima.crawler.service.impl;

import com.heima.crawler.service.CrawlerNewsAdditionalService;
import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.core.parse.impl.CrawlerParseItem;
import com.heima.model.crawler.pojos.ClNewsAdditional;
import com.heima.model.mappers.crawerls.ClNewsAdditionalMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@SuppressWarnings("all")
public class CrawlerNewsAdditionalServiceImpl implements CrawlerNewsAdditionalService {

    @Autowired
    private ClNewsAdditionalMapper clNewsAdditionalMapper;


    @Override
    public void saveAdditional(ClNewsAdditional clNewsAdditional) {
        clNewsAdditionalMapper.insertSelective(clNewsAdditional);
    }

    @Override
    public List<ClNewsAdditional> queryListByNeedUpdate(Date currentDate) {
        return clNewsAdditionalMapper.selectListByNeedUpdate(currentDate);
    }

    @Override
    public List<ClNewsAdditional> queryList(ClNewsAdditional clNewsAdditional) {
        return clNewsAdditionalMapper.selectList(clNewsAdditional);
    }

    @Override
    public boolean checkExist(String url) {
        ClNewsAdditional clNewsAdditional = new ClNewsAdditional();
        clNewsAdditional.setUrl(url);
        List<ClNewsAdditional> clNewsAdditionalList = clNewsAdditionalMapper.selectList(clNewsAdditional);
        if (null != clNewsAdditionalList && !clNewsAdditionalList.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public ClNewsAdditional getAdditionalByUrl(String url) {
        ClNewsAdditional clNewsAdditional = new ClNewsAdditional();
        clNewsAdditional.setUrl(url);
        List<ClNewsAdditional> additionalList = queryList(clNewsAdditional);
        if (null != additionalList && !additionalList.isEmpty()) {
            return additionalList.get(0);
        }
        return null;
    }

    @Override
    public boolean isExistsUrl(String url) {
        boolean isExistsUrl = false;
        if (StringUtils.isNotEmpty(url)) {
            ClNewsAdditional clNewsAdditional = getAdditionalByUrl(url);
            if (null != clNewsAdditional) {
                isExistsUrl = true;
            }
        }
        return isExistsUrl;
    }

    @Override
    public void updateAdditional(ClNewsAdditional clNewsAdditional) {
        clNewsAdditionalMapper.updateByPrimaryKeySelective(clNewsAdditional);
    }

    @Override
    public List<ParseItem> toParseItem(List<ClNewsAdditional> additionalList) {
        List<ParseItem> parseItemList = new ArrayList<ParseItem>();
        if (null != additionalList && !additionalList.isEmpty()) {
            for (ClNewsAdditional additional : additionalList) {
                ParseItem parseItem = toParseItem(additional);
                if (null != parseItem) {
                    parseItemList.add(parseItem);
                }
            }
        }
        return parseItemList;
    }

    private ParseItem toParseItem(ClNewsAdditional additional) {
        CrawlerParseItem crawlerParseItem = null;
        if (null != additional) {
            crawlerParseItem = new CrawlerParseItem();
            crawlerParseItem.setUrl(additional.getUrl());
        }
        return crawlerParseItem;
    }

    @Override
    public List<ParseItem> queryIncrementParseItem(Date currentDate) {
        List<ClNewsAdditional> clNewsAdditionalList = queryListByNeedUpdate(currentDate);
        List<ParseItem> parseItemList = toParseItem(clNewsAdditionalList);
        return parseItemList;
    }
}
