package com.heima.crawler.process.original;

import com.heima.crawler.process.AbstractProcessFlow;
import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.enums.CrawlerEnum;

import java.util.List;

/**
 * 初始化url
 */
public abstract class AbstractOriginalDataProcess extends AbstractProcessFlow {
    @Override
    public void handel(ProcessFlowData processFlowData) {

    }

    @Override
    public CrawlerEnum.ComponentType getComponentType() {
        return null;
    }

    /**
     * 解析初始的数据
     *
     * @return
     */
    public abstract List<ParseItem> parseOriginalRequestData(ProcessFlowData processFlowData);
}
