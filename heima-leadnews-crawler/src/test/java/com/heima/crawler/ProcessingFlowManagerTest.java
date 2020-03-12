package com.heima.crawler;

import com.heima.crawler.manager.ProcessingFlowManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = CrawlerJarApplication.class)
@RunWith(SpringRunner.class)
public class ProcessingFlowManagerTest {

    @Autowired
    private ProcessingFlowManager processingFlowManager;

    @Test
    public void test(){
        try {
            processingFlowManager.handel();
            Thread.sleep(Integer.MAX_VALUE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
