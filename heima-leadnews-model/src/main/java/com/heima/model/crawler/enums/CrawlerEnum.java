package com.heima.model.crawler.enums;

public class CrawlerEnum {

    /**
     * 处理方式
     * FORWARD 正向，REVERSE 反向
     */
    public enum HandelType {
        FORWARD, REVERSE
    }

    /**
     * 抓取规则类型
     */
    public enum ParseRuleType {
        REGULAR, XPATH, CSS
    }

    /**
     * 返回数据类型
     */
    public enum ReturnDataType {
        HTML, JSON
    }


    /**
     * 抓取类型
     */
    public enum ComponentType {
        NORMAL, PAGEPROCESSOR, PIPELINE, DOWNLOAD
    }

    public enum HtmlType {
        P_TAG("p", "text"),
        A_TAG("a", "link"),
        IMG_TAG("img", "image"),
        H1_TAG("h1", "text"),
        H2_TAG("h2", "text"),
        H3_TAG("h3", "text"),
        H4_TAG("h4", "text"),
        H5_TAG("h5", "text"),
        H6_TAG("h6", "text"),
        PRE_TAG("pre", "text"),
        CODE_TAG("code", "code"),
        STRONG_TAG("strong", "text");

        /**
         * 标签名称
         */
        private String labelName;
        /**
         * 数据类型
         */
        private String dataType;


        HtmlType(String labelName, String dataType) {
            this.labelName = labelName;
            this.dataType = dataType;
        }

        public String getLabelName() {
            return labelName;
        }

        public String getDataType() {
            return dataType;
        }
    }


}
