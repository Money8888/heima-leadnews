package com.heima.crawler.utils;

import com.alibaba.fastjson.JSON;
import com.heima.model.crawler.core.label.HtmlLabel;
import com.heima.model.crawler.core.label.HtmlStyle;
import com.heima.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Html解析工具
 * 将html转换为特定格式
 */
@Log4j2
public class HtmlParser {

    /**
     * 需要处理的Html 标签
     */
    private final CrawlerEnum.HtmlType[] specialHtmlTypeArray =
            new CrawlerEnum.HtmlType[]{
                    CrawlerEnum.HtmlType.A_TAG,
                    CrawlerEnum.HtmlType.CODE_TAG,
                    CrawlerEnum.HtmlType.H1_TAG,
                    CrawlerEnum.HtmlType.H2_TAG,
                    CrawlerEnum.HtmlType.H3_TAG,
                    CrawlerEnum.HtmlType.H4_TAG,
                    CrawlerEnum.HtmlType.H5_TAG};

    /**
     * 默认css 设置
     */
    private Map<String, HtmlStyle> defaultStyleMap = null;

    /**
     * css 表达式
     */
    private String cssExpression = null;


    public static HtmlParser getHtmlParser(String cssExpression, Map<String, HtmlStyle> defaultStyleMap) {
        return new HtmlParser(cssExpression, defaultStyleMap);
    }


    public HtmlParser(String cssExpression, Map<String, HtmlStyle> defaultStyleMap) {
        this.cssExpression = cssExpression;
        this.defaultStyleMap = defaultStyleMap;
    }

    /**
     * 解析html 内容
     *
     * @param content
     * @return
     */
    public List<HtmlLabel> parseHtml(String content) {
        long currentTime = System.currentTimeMillis();
        log.info("开始解析文章内容");
        Document document = Jsoup.parse(content);
        Elements elements = document.select(cssExpression);
        List<HtmlLabel> htmlLabelList = parseElements(elements);
        log.info("解析文章内容完成，耗时：{}", System.currentTimeMillis() - currentTime);
        return htmlLabelList;
    }


    /**
     * 解析Html内容并转换为json数据
     *
     * @param content
     * @return
     */
    public String parserHtmlToJson(String content) {
        List<HtmlLabel> htmlLabelList = parseHtml(content);
        return JSON.toJSONString(htmlLabelList);
    }

    /**
     * 解析Html dom树
     *
     * @param elements
     * @return
     */
    private List<HtmlLabel> parseElements(Elements elements) {
        List<HtmlLabel> htmlLabelList = new ArrayList<HtmlLabel>();
        elements.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                List<HtmlLabel> labelList = parserElement(element, new ParserCallBack() {
                    @Override
                    public void callBack(Elements elements) {
                        parseElements(elements);
                    }
                });
                htmlLabelList.addAll(labelList);
            }
        });
        return htmlLabelList;
    }

    /**
     * 解析Html 元素
     *
     * @param element
     * @param callBack
     * @return
     */
    private List<HtmlLabel> parserElement(Element element, ParserCallBack callBack) {
        List<HtmlLabel> htmlLabelList = new ArrayList<HtmlLabel>();
        //校验元素是否需要处理
        if (isNeedHandel(element)) {
            HtmlLabel htmlLabel = parseNodeByByElement(element);
            htmlLabelList.add(htmlLabel);
        } else {
            //获取元素所有的子节点
            List<Node> childNodes = element.childNodes();
            //解析节点列表
            List<HtmlLabel> list = parserNodeList(childNodes, callBack);
            htmlLabelList.addAll(list);
        }
        return htmlLabelList;
    }

    /**
     * 解析 Html 节点列表
     *
     * @param nodeList
     * @param callBack
     * @return
     */
    private List<HtmlLabel> parserNodeList(List<Node> nodeList, ParserCallBack callBack) {
        List<HtmlLabel> htmlLabelList = new ArrayList<HtmlLabel>();
        if (null != nodeList && !nodeList.isEmpty()) {
            List<Element> elementList = new ArrayList<Element>();
            for (Node node : nodeList) {
                //检查节点是否需要处理
                if (isNeedHandel(node)) {
                    //解析node节点
                    HtmlLabel htmlLabel = parseNode(node);
                    if (null != htmlLabel) {
                        htmlLabelList.add(htmlLabel);
                    }
                } else {
                    //如果不需要处理并且是element元素就加入list中交给回调方法进行递归处理
                    if (node instanceof Element) {
                        elementList.add((Element) node);
                    }
                }
            }
            if (!elementList.isEmpty()) {
                //调用回调方法进行递归处理
                callBack.callBack(new Elements(elementList));
            }
        }
        return htmlLabelList;
    }

    /**
     * 解析Html 节点
     *
     * @param node
     * @return
     */
    private HtmlLabel parseNode(Node node) {
        HtmlLabel htmlLabel = null;
        if (null != node) {
            if (node instanceof TextNode) {
                htmlLabel = parseNodeByTextNode((TextNode) node);
            } else if (node instanceof Element) {
                htmlLabel = parseNodeByByElement((Element) node);
            }
        }
        return htmlLabel;
    }

    /**
     * 获取文本类型的数据节点
     *
     * @param textNode
     * @return
     */
    private HtmlLabel parseNodeByTextNode(TextNode textNode) {
        HtmlLabel htmlLabel = null;
        if (null != textNode) {
            String text = textNode.getWholeText();
            if (StringUtils.isNotBlank(text)) {
                htmlLabel = new HtmlLabel();
                htmlLabel.setValue(textNode.getWholeText());
                htmlLabel.setType("text");
            }
        }
        return htmlLabel;
    }

    /**
     * 解析Element 元素
     *
     * @param element
     * @return
     */
    private HtmlLabel parseNodeByByElement(Element element) {
        HtmlLabel htmlLabel = null;
        if (null != element) {
            String tagName = element.tagName();
            if (CrawlerEnum.HtmlType.A_TAG.getLabelName().equals(tagName)) {
                // explanLabel = getExplanLabelByaLink(element);
            } else if (CrawlerEnum.HtmlType.IMG_TAG.getLabelName().equals(tagName)) {
                htmlLabel = parseNodeByImage(element);
            } else if (CrawlerEnum.HtmlType.CODE_TAG.getLabelName().equals(tagName)) {
                htmlLabel = parseNodeByCode(element);
            } else {
                htmlLabel = parseNodeByOther(element);
            }
        }
        return htmlLabel;
    }


    /**
     * 获取A 标签的链接
     *
     * @param element
     * @return
     */
    private HtmlLabel parseNodeByByaLink(Element element) {
        HtmlLabel htmlLabel = null;
        if (null != element) {
            String link = element.attr("href");
            String text = element.ownText();
            htmlLabel = new HtmlLabel();
            htmlLabel.setValue(text);
            // explanLabel.setLink(link);
            htmlLabel.setType(CrawlerEnum.HtmlType.A_TAG.getDataType());
        }
        return htmlLabel;
    }

    /**
     * 获取图片的信息
     *
     * @param element
     * @return
     */
    private HtmlLabel parseNodeByImage(Element element) {
        HtmlLabel htmlLabel = null;
        if (null != element) {
            String src = element.attr("src");
            src = imageUrlHandel(src);
            String width = element.attr("width");
            String height = element.attr("height");
            htmlLabel = new HtmlLabel();
            HtmlStyle htmlStyle = new HtmlStyle();
            htmlStyle.addStyle("width", width + "px");
            htmlStyle.addStyle("height", height + "px");
            htmlLabel.setValue(src);
            htmlLabel.setStyle(htmlStyle.getCssStyle());
            htmlLabel.setType(CrawlerEnum.HtmlType.IMG_TAG.getDataType());
        }
        return htmlLabel;
    }

    /**
     * 处理图片URL带参数问题
     *
     * @param src
     * @return
     */
    private String imageUrlHandel(String src) {
        if (StringUtils.isNotEmpty(src) && src.contains("?")) {
            src = src.substring(0, src.indexOf("?"));
        }
        return src;
    }

    /**
     * 获取代码的数据
     *
     * @param element
     * @return
     */
    private HtmlLabel parseNodeByCode(Element element) {
        HtmlLabel htmlLabel = null;
        if (null != element) {
            String text = element.ownText();
            htmlLabel = new HtmlLabel();
            htmlLabel.setValue(text);
            htmlLabel.setType(CrawlerEnum.HtmlType.CODE_TAG.getDataType());
        }
        return htmlLabel;
    }

    /**
     * 其他数据处理
     *
     * @param element
     * @return
     */
    private HtmlLabel parseNodeByOther(Element element) {
        HtmlLabel htmlLabel = null;
        if (null != element) {
            HtmlStyle htmlStyle = defaultStyleMap.get(element.tagName());
            String text = element.ownText();
            htmlLabel = new HtmlLabel();
            htmlLabel.setValue(text);
            htmlLabel.setType("text");
            if (null != htmlStyle) {
                htmlLabel.setStyle(htmlStyle.getCssStyle());
            }

        }
        return htmlLabel;
    }

    /**
     * 检查节点否需要处理
     *
     * @return
     */
    private boolean isNeedHandel(Node node) {
        boolean flag = false;
        //没有子节点
        if (node.childNodes().isEmpty()) {
            flag = true;
        } else {
            if (null != node && node instanceof Element) {
                Element element = (Element) node;
                flag = isNeedHandel(element);
            }
        }
        return flag;
    }

    /**
     * 校验是否需要进行处理
     *
     * @param element
     * @return
     */
    private boolean isNeedHandel(Element element) {
        boolean flag = false;
        if (null != element) {
            String tagName = element.tagName();
            for (CrawlerEnum.HtmlType htmlType : specialHtmlTypeArray) {
                if (htmlType.getLabelName().toLowerCase().equals(tagName.toLowerCase())) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }


    /**
     * 内部接口
     */
    private interface ParserCallBack {
        void callBack(Elements elements);
    }

    public Map<String, HtmlStyle> getDefaultStyleMap() {
        return defaultStyleMap;
    }

    public void setDefaultStyleMap(Map<String, HtmlStyle> defaultStyleMap) {
        this.defaultStyleMap = defaultStyleMap;
    }

    public String getCssExpression() {
        return cssExpression;
    }

    public void setCssExpression(String cssExpression) {
        this.cssExpression = cssExpression;
    }
}
