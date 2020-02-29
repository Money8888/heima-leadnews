package com.heima.model.crawler.core.label;

import java.io.Serializable;

public class HtmlLabel implements Serializable {

    /**
     * 解析的数据类型
     */
    private String type;

    /**
     * 标签内容
     */
    private String value;

    /**
     * 设置样式
     */
    private String style;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
