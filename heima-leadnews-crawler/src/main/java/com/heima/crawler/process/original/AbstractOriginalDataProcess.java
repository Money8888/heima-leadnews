package com.heima.crawler.process.original;

import com.heima.crawler.process.AbstractProcessFlow;
import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 初始化url
 */
@Slf4j
public abstract class AbstractOriginalDataProcess extends AbstractProcessFlow {
    @Override
    public void handel(ProcessFlowData processFlowData) {
        //初始化URL数据列表
        List<ParseItem> initialDataList = processFlowData.getParseItemList();
        if (!(initialDataList != null && !initialDataList.isEmpty())) {
            //解析初始URL获取需要抓取的URL链接
            log.info("获取初始化URL列表");
            initialDataList = parseOriginalRequestData(processFlowData);
            log.info("初始化URL列表完成，有效URL数量：{}", initialDataList.size());
            //初始化方法的一些处理，这里面是一个空方法，没有任何实现
            initialDataListHandel(initialDataList);
        }
        if (null != initialDataList && !initialDataList.isEmpty()) {

            //数据添加到Spider中
            addSpiderRequest(initialDataList);
        } else {
            log.error("没有有效的初始化URL列表");
        }

        //后置处理，这里是一个空的实现
        postprocess(processFlowData);
    }

    @Override
    public CrawlerEnum.ComponentType getComponentType() {
        return CrawlerEnum.ComponentType.NORMAL;
    }

    /**
     * 解析初始的数据
     *
     * @return
     */
    public abstract List<ParseItem> parseOriginalRequestData(ProcessFlowData processFlowData);

    /**
     * 初始数据存储
     *
     * @param initialDataList
     */
    public void initialDataListHandel(List<ParseItem> initialDataList) {
    }

    /**
     * 后置处理
     *
     * @param processFlowData
     */
    public void postprocess(ProcessFlowData processFlowData) {

    }
}
