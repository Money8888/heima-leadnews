package com.heima.common.zookeeper;

import com.heima.common.zookeeper.sequence.ZkSequenceEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Sequences {

    @Autowired
    private ZookeeperClient zookeeperClient;

    public Long sequenceApLikes() {
        return this.zookeeperClient.sequence(ZkSequenceEnum.AP_LIKES);
    }

    public Long sequenceApReadBehavior(){
        return this.zookeeperClient.sequence(ZkSequenceEnum.AP_READ_BEHAVIOR);
    }

    public Long sequenceApCollection(){
        return this.zookeeperClient.sequence(ZkSequenceEnum.AP_COLLECTION);
    }

    public Long sequenceApUserFollow(){
        return this.zookeeperClient.sequence(ZkSequenceEnum.AP_USER_FOLLOW);
    }

    public Long sequenceApUserFan(){
        return this.zookeeperClient.sequence(ZkSequenceEnum.AP_USER_FAN);
    }
}
