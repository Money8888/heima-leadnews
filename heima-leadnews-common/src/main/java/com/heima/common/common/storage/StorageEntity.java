package com.heima.common.common.storage;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个存储的实体
 */
@Setter
@Getter
public class StorageEntity {

    /**
     * 存储类型的列表
     * 一个实体可以存储多个数据列表
     */
    private List<StorageData> dataList = new ArrayList<StorageData>();

    /**
     * 添加一个存储数据
     * @param storageData
     */
    public void addStorageData(StorageData storageData) {
        dataList.add(storageData);
    }
}
