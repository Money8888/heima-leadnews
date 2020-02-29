package com.heima.behavior;


import com.heima.common.zookeeper.Sequences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = BehaviorJarApplication.class)
@RunWith(SpringRunner.class)
public class ZookeeperTest {

    @Autowired
    private Sequences sequences;

    @Test
    public void Test(){
        for(int i = 0; i < 100; i ++){
            Long aLong = sequences.sequenceApLikes();
            System.out.println("+++++++++++++++++++");
            System.out.println(aLong);
        }
    }
}
