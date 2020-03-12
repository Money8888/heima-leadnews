package com.heima.common.common.util;

import java.util.List;

public class HMStringUtils {
    /**
     * 获取固定长度的字符串
     * getFixedLengthStr("ABCDEFGH",10) ABCDEFGH
     * getFixedLengthStr("ABCDEFGH",3)  ABC
     *
     * @param str
     * @param length
     * @return
     */
    public static String getFixedLengthStr(String str, int length) {
        String fixedStr = null;
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(str) && length > 0) {
            if (str.length() <= length) {
                fixedStr = str;
            } else {
                fixedStr = str.substring(0, length);
            }
        }
        return fixedStr;
    }

    /**
     * 获取固定长度的list
     *
     * @param contentList 传入的list长度
     * @param length      最大长度
     * @param fixedLength 固定长度
     * @return
     */
    public static List<String> getFixedLengthContentList(List<String> contentList, int length, int fixedLength) {
        if (null != contentList && !contentList.isEmpty() && length > 0) {
            int breakIndex = -1;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < contentList.size(); i++) {
                stringBuilder.append(contentList.get(i));
                if ((stringBuilder.length() + i + fixedLength) >= length) {
                    breakIndex = i;
                    break;
                }
            }
            if (breakIndex >= 0) {
                contentList = contentList.subList(0, breakIndex);
            }
        }
        return contentList;
    }


    public static String arrayToStr(String[] array, String split) {
        String value = null;
        if (null != array && array.length > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String str : array) {
                stringBuilder.append(str).append(split);
            }
            if (stringBuilder.length() > 0) {
                value = stringBuilder.substring(0, stringBuilder.length() - 1);
            }
        }
        return value;
    }


    public static String listToStr(List<String> array, String split) {
        String value = null;
        if (null != array && !array.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String str : array) {
                stringBuilder.append(str).append(split);
            }
            if (stringBuilder.length() > 0) {
                value = stringBuilder.substring(0, stringBuilder.length() - 1);
            }
        }
        return value;
    }

    public static String toString(Object value) {
        String str = "";
        if (null != value) {
            str = value.toString();
        }
        return str;
    }
}
