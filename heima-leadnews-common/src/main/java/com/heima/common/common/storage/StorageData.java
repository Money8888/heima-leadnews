package com.heima.common.common.storage;

import com.heima.utils.common.DataConvertUtils;
import com.heima.utils.common.ReflectUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 存储数据Data
 */
@Setter
@Getter
@ToString
public class StorageData {
    /**
     * 目标class 类名称
     */
    private String targetClassName;
    /**
     * 存储的字段列表
     */
    private List<StorageEntry> entryList = new ArrayList<StorageEntry>();

    /**
     * 添加一个entry
     *
     * @param entry
     */
    public void addStorageEntry(StorageEntry entry) {
        entryList.add(entry);
    }

    /**
     * 添加一个entry
     *
     * @param key
     * @param value
     */
    public void addStorageEntry(String key, String value) {
        entryList.add(new StorageEntry(key, value));
    }

    /**
     * 根据Map 添加entry
     *
     * @param map
     */
    public void putHBaseEntry(Map<String, String> map) {
        if (null != map && !map.isEmpty()) {
            map.forEach((k, v) -> addStorageEntry(new StorageEntry(k, v)));
        }
    }

    /**
     * 获取所有的Column 数组
     *
     * @return
     */
    public String[] getColumns() {
        List<String> columnList = entryList.stream().map(StorageEntry::getKey).collect(Collectors.toList());
        if (null != columnList && !columnList.isEmpty()) {
            return columnList.toArray(new String[columnList.size()]);
        }
        return null;
    }

    /**
     * 获取所有的值数字
     *
     * @return
     */
    public String[] getValues() {
        List<String> valueList = entryList.stream().map(StorageEntry::getValue).collect(Collectors.toList());
        if (null != valueList && !valueList.isEmpty()) {
            return valueList.toArray(new String[valueList.size()]);
        }
        return null;
    }

    /**
     * 获取一个Map
     *
     * @return
     */
    public Map<String, Object> getMap() {
        Map<String, Object> entryMap = new HashMap<String, Object>();
        entryList.forEach(entry -> entryMap.put(entry.getKey(), entry.getValue()));
        return entryMap;
    }


    /**
     * 将当前的StorageData 转换为具体的对象
     *
     * @return
     */
    public Object getObjectValue() {
        Object bean = null;
        if (StringUtils.isNotEmpty(targetClassName) && null != entryList && !entryList.isEmpty()) {
            bean = ReflectUtils.getClassForBean(targetClassName);
            if (null != bean) {
                for (StorageEntry entry : entryList) {
                    Object value = DataConvertUtils.convert(entry.getValue(), ReflectUtils.getFieldAnnotations(bean, entry.getKey()));
                    ReflectUtils.setPropertie(bean, entry.getKey(), value);
                }
            }
        }
        return bean;
    }

    /**
     * 将一个Bean 转换未为StorageData
     *
     * @param bean
     * @return
     */
    public static StorageData getStorageData(Object bean) {
        StorageData hbaseData = null;
        if (null != bean) {
            hbaseData = new StorageData();
            hbaseData.setTargetClassName(bean.getClass().getName());
            hbaseData.setEntryList(getStorageEntryList(bean));

        }
        return hbaseData;
    }

    /**
     * 根据bean 获取entry 列表
     *
     * @param bean
     * @return
     */
    private static List<StorageEntry> getStorageEntryList(Object bean) {
        PropertyDescriptor[] propertyDescriptorArray = ReflectUtils.getPropertyDescriptorArray(bean);
        return Arrays.asList(propertyDescriptorArray).stream().map(propertyDescriptor -> {
            String key = propertyDescriptor.getName();
            Object value = ReflectUtils.getPropertyDescriptorValue(bean, propertyDescriptor);
            value = DataConvertUtils.unConvert(value, ReflectUtils.getFieldAnnotations(bean, propertyDescriptor));
            return new StorageEntry(key, DataConvertUtils.toString(value));
        }).collect(Collectors.toList());
    }

}