package com.heima.common.zookeeper.sequence;

import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 用于封装程序在运行时每个表对应的自增器
 * 通过分布式原子自增类（DistributedAtomicLong）实现
 * 每500毫秒重试3次后仍然生成失败则返回null
 */
public class ZkSequence {

    RetryPolicy retryPolicy = new ExponentialBackoffRetry(500, 3);

    DistributedAtomicLong distributedAtomicLong;

    public ZkSequence(CuratorFramework client, String counterPath) {
         this.distributedAtomicLong = new DistributedAtomicLong(client, counterPath, retryPolicy);
    }

    /**
     * 生成序列
     * @return
     * @throws Exception
     */

    public Long sequence() throws Exception {
        AtomicValue<Long> increment = distributedAtomicLong.increment();
        if(increment.succeeded()){
            return increment.postValue();
        }else {
            return null;
        }
    }
}
