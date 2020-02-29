package com.heima.common.common.util;

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
}
