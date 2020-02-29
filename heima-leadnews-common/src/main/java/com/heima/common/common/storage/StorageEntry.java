package com.heima.common.common.storage;

import lombok.Getter;
import lombok.Setter;

/**
 * 存储Entry
 * k-v 结构保存一个对象的字段的字段名和值
 */
@Setter
@Getter
public class StorageEntry {
    /**
     * 空的构造方法
     */
    public StorageEntry() {
    }

    /**
     * 构造方法
     *
     * @param key
     * @param value
     */
    public StorageEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 字段的Key
     */
    private String key;

    /**
     * 字段的Value
     */
    private String value;

}
