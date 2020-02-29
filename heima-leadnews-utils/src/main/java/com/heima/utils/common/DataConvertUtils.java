package com.heima.utils.common;

import com.heima.model.annotation.DateConvert;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据类转换
 */
public class DataConvertUtils {

    /**
     * 自定义数据类型转换
     *
     * @param value
     * @param annotations
     * @return
     */
    public static Object convert(Object value, Annotation[] annotations) {
        Object convertValue = value;
        DateConvert dateConvert = getDateConvert(annotations);
        if (null != dateConvert) {
            convertValue = dateConvert(value, dateConvert);
        }
        return convertValue;
    }


    public static Object unConvert(Object value, Annotation[] annotations) {
        Object convertValue = value;
        DateConvert dateConvert = getDateConvert(annotations);
        if (null != dateConvert) {
            convertValue = unDateConvert(value, dateConvert);
        }
        return convertValue;
    }

    /**
     * value
     *
     * @param value
     * @param dateConvert
     * @return
     */
    private static Object unDateConvert(Object value, DateConvert dateConvert) {
        Object unConverValue = null;
        if (value instanceof Date && null != dateConvert) {
            String datePattern = dateConvert.value();
            if (StringUtils.isNotEmpty(datePattern)) {
                unConverValue = DateUtils.dateToString((Date) value, datePattern);
            }
        }
        return unConverValue;
    }

    /**
     * Date 数据类型转换
     *
     * @param value
     * @param dateConvert
     * @return
     */
    private static Object dateConvert(Object value, DateConvert dateConvert) {
        Object dateValue = null;
        String strDateValue = toString(value);
        if (StringUtils.isNotEmpty(strDateValue) && null != dateConvert) {
            String datePattern = dateConvert.value();
            if (StringUtils.isNotEmpty(datePattern)) {
                dateValue = DateUtils.stringToDate(strDateValue, datePattern);
            }
        }
        return dateValue;
    }

    /**
     * 获取DateConvert 转换注解
     *
     * @param annotations
     * @return
     */
    public static DateConvert getDateConvert(Annotation[] annotations) {
        DateConvert dateConvert = null;
        if (null != annotations && annotations.length > 0) {
            List<Annotation> annotationList = Arrays.asList(annotations).stream().filter(anno -> anno instanceof DateConvert).collect(Collectors.toList());
            if (null != annotationList && !annotationList.isEmpty()) {
                dateConvert = (DateConvert) annotationList.get(0);
            }
        }
        return dateConvert;
    }


    public static String toString(Object value) {
        String serValue = "";
        if (null != value) {
            serValue = value.toString();
        }
        return serValue;
    }
}
