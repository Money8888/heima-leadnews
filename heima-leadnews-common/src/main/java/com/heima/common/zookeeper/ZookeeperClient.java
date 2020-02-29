package com.heima.common.zookeeper;

import com.google.common.collect.Maps;
import com.heima.common.zookeeper.sequence.ZkSequence;
import com.heima.common.zookeeper.sequence.ZkSequenceEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Map;

@Setter
@Getter
public class ZookeeperClient {

    private static Logger logger = LoggerFactory.getLogger(ZookeeperClient.class);

    // zk host地址
    private String host;

    // zk自增存储node
    private String sequencePath;

    // 重试休眠时间
    private final int SLEEP_TIME_MS = 1000;
    // 最大重试1000次
    private final int MAX_RETRIES = 1000;
    //会话超时时间
    private final int SESSION_TIMEOUT = 30 * 1000;
    //连接超时时间
    private final int CONNECTION_TIMEOUT = 3 * 1000;

    // 创建连接客户端
    private CuratorFramework client = null;

    // 创建map集合，key为表名，value为序列化对象
    // newConcurrentMap线程安全，map不轻易修改
    Map<String, ZkSequence> zkSequenceMap = Maps.newConcurrentMap();

    public ZookeeperClient(String host, String sequencePath) {
        this.host = host;
        this.sequencePath = sequencePath;
    }

    // 初始化客户端
    // 被@PostConstruct修饰的方法会在构造函数之后，在服务器加载Servlet的时候运行，并且只会被服务器调用一次
    @PostConstruct
    public void init(){
        this.client = CuratorFrameworkFactory.builder()
                .connectString(this.host)
                .connectionTimeoutMs(CONNECTION_TIMEOUT)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(SLEEP_TIME_MS, MAX_RETRIES))
                .build();
        // 启动
        this.client.start();
        this.initZkSequence();
    }

    public void initZkSequence(){
        ZkSequenceEnum[] list = ZkSequenceEnum.values();
        for(int i = 0; i < list.length; i++){
            // 获取表名
            String name = list[i].name();
            // 拼串成路径
            String path = this.sequencePath + name;
            // 创建zk序列化对象
            ZkSequence seq = new ZkSequence(this.client, path);
            zkSequenceMap.put(name, seq);
        }
    }

    /**
     * 生成分布式id的sequence
     * @param tableName 表名
     * @return
     */
    public Long sequence(ZkSequenceEnum tableName){
        try {
            ZkSequence seq = zkSequenceMap.get(tableName.name());
            if(seq != null){
                return seq.sequence();
            }
        }catch (Exception e){
            // 错误信息记录日志
            logger.error("获取[{}]Sequence错误:{}", tableName, e);
            e.printStackTrace();
        }
        return null;
    }
}
