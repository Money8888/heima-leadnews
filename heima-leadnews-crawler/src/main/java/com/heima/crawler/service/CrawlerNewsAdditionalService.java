package com.heima.crawler.service;

import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.pojos.ClNewsAdditional;

import java.util.Date;
import java.util.List;

public interface CrawlerNewsAdditionalService {

    void saveAdditional(ClNewsAdditional clNewsAdditional);

    public List<ClNewsAdditional> queryListByNeedUpdate(Date currentDate);

    List<ClNewsAdditional> queryList(ClNewsAdditional clNewsAdditional);

    public boolean checkExist(String url);

    public ClNewsAdditional getAdditionalByUrl(String url);

    /**
     * 是否是已存在的URL
     *
     * @return
     */
    public boolean isExistsUrl(String url);

    public void updateAdditional(ClNewsAdditional clNewsAdditional);

    public List<ParseItem> toParseItem(List<ClNewsAdditional> additionalList);

    public List<ParseItem> queryIncrementParseItem(Date currentDate);
}