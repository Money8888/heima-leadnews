package com.heima.model.crawler.core.label;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class HtmlStyle {
    private Map<String, String> styleMap = new HashMap<>();

    public void addStyle(String key, String value) {
        styleMap.put(key, value);
    }

    public void addStyle(Map<String, String> map) {
        styleMap.putAll(map);
    }

    public String getCssStyle() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : styleMap.entrySet()) {
            sb.append(entry.getKey()).append(":'").append(entry.getValue()).append("',");
        }
        return StringUtils.removeEnd(sb.toString(), ",");
    }

    public Map<String, String> getStyleMap() {
        return styleMap;
    }

    public void setStyleMap(Map<String, String> styleMap) {
        this.styleMap = styleMap;
    }
}
