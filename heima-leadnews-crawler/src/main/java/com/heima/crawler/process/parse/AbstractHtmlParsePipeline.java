package com.heima.crawler.process.parse;

import com.heima.crawler.helper.CrawlerHelper;
import com.heima.crawler.process.AbstractProcessFlow;
import com.heima.crawler.process.entity.ProcessFlowData;
import com.heima.model.crawler.core.label.HtmlStyle;
import com.heima.model.crawler.core.parse.ParseItem;
import com.heima.model.crawler.enums.CrawlerEnum;
import com.heima.utils.common.ReflectUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.HashMap;
import java.util.Map;


/**
 * Html 解析抽抽象类，定义了公用的方法以及抽象模板
 * <p>
 * <p>
 * Pipeline负责抽取结果的处理，包括计算、持久化到文件、数据库等。WebMagic默认提供了“输出到控制台”和“保存到文件”两种结果处理方案。
 * <p>
 * Pipeline定义了结果保存的方式，如果你要保存到指定数据库，则需要编写对应的Pipeline。对于一类需求一般只需编写一个Pipeline。
 *
 * @param <T>
 */
@Log4j2
public abstract class AbstractHtmlParsePipeline<T> extends AbstractProcessFlow implements Pipeline {

    @Autowired
    private CrawlerHelper crawlerHelper;

    @Override
    public void handel(ProcessFlowData processFlowData) {

    }

    @Override
    public CrawlerEnum.ComponentType getComponentType() {
        return CrawlerEnum.ComponentType.PIPELINE;
    }


    /**
     * 这里传入的是处理好的对象
     * 对结构数据进行清洗以及存储
     * 是 Pipeline的主要入口方法
     *
     * @param resultItems ResultItems保存了抽取结果，它是一个Map结构，
     * @param task
     */
    @Override
    public void process(ResultItems resultItems, Task task) {
        long currentTimeMillis = System.currentTimeMillis();
        String url = resultItems.getRequest().getUrl();
        String documentType = crawlerHelper.getDocumentType(resultItems.getRequest());
        String handelType = crawlerHelper.getHandelType(resultItems.getRequest());
        log.info("开始解析抽取后的数据，url:{},handelType:{}", url, handelType);
        if(!CrawlerEnum.DocumentType.PAGE.name().equals(documentType)){
            log.error("不符合的文档类型，url:{},documentType:{},handelType:{}", url, documentType, handelType);
            return;
        }
        ParseItem parseItem = crawlerHelper.getParseItem(resultItems.getRequest());
        if(parseItem != null && StringUtils.isNotEmpty(url)){
            Map<String, Object> itemsAll = resultItems.getAll();
            // 前置参数处理
            preParameterHandel(itemsAll);
            if(url.equals(parseItem.getInitialUrl())){
                // 通过反射设置属性
                ReflectUtils.setPropertie(parseItem, itemsAll, true);
                parseItem.setHandelType(crawlerHelper.getHandelType(resultItems.getRequest()));
                handelHtmlData((T) parseItem);
            }
        }
        log.info("解析抽取后的数据完成，url:{},handelType:{},耗时：{}", url, handelType, System.currentTimeMillis() - currentTimeMillis);
    }

    protected abstract void handelHtmlData(T parseItem);

    protected abstract void preParameterHandel(Map<String, Object> itemsAll);

    /**
     * 获取解析表达式
     *
     * @return
     */
    public String getParseExpression() {
        return "p,pre,h1,h2,h3,h4,h5";
    }

    /**
     * 获取默认的Html 样式
     *
     * @return
     */
    public Map<String, HtmlStyle> getDefHtmlStyleMap() {
        Map<String, HtmlStyle> styleMap = new HashMap<String, HtmlStyle>();
        //h1 数据添加
        HtmlStyle h1Style = new HtmlStyle();
        h1Style.addStyle("font-size", "22px");
        h1Style.addStyle("line-height", "24px");
        styleMap.put("h1", h1Style);
        //h2 数据添加
        HtmlStyle h2Style = new HtmlStyle();
        h2Style.addStyle("font-size", "18px");
        h2Style.addStyle("line-height", "20px");
        styleMap.put("h2", h2Style);
        //h3 数据添加
        HtmlStyle h3Style = new HtmlStyle();
        h3Style.addStyle("font-size", "16px");
        h3Style.addStyle("line-height", "18px");
        styleMap.put("h3", h3Style);
        //h4 数据添加
        HtmlStyle h4Style = new HtmlStyle();
        h4Style.addStyle("font-size", "14px");
        h4Style.addStyle("line-height", "16px");
        styleMap.put("h4", h4Style);
        //h5 数据添加
        HtmlStyle h5Style = new HtmlStyle();
        h5Style.addStyle("font-size", "12px");
        h5Style.addStyle("line-height", "14px");
        styleMap.put("h5", h5Style);
        //h6 数据添加
        HtmlStyle h6Style = new HtmlStyle();
        h6Style.addStyle("font-size", "10px");
        h6Style.addStyle("line-height", "12px");
        styleMap.put("h6", h6Style);
        return styleMap;
    }

}
