package com.heima.crawler;

import com.heima.crawler.service.AdLabelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AdLabelServiceTest {

    @Autowired
    private AdLabelService labelService;

    @Test
    public void testGetLabelIds(){
        String labelIds = labelService.getLabelIds("java,web,xxxxxxx");
        System.out.println(labelIds);
    }

    @Test
    public void testGetAdChannelByLabelIds(){
        Integer adChannelByLabelIds = labelService.getAdChannelByLabelIds("9,10");
        System.out.println(adChannelByLabelIds);
    }
}