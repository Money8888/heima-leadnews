package com.heima.crawler;

import com.heima.crawler.service.CrawlerNewsAdditionalService;
import com.heima.model.crawler.pojos.ClNewsAdditional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CrawlerNewsAdditionalServiceTest {

    @Autowired
    private CrawlerNewsAdditionalService crawlerNewsAdditionalService;

    @Test
    public void testQueryList(){
        ClNewsAdditional clNewsAdditional = new ClNewsAdditional();
        clNewsAdditional.setUrl("https://blog.csdn.net/weixin_43976602/article/details/96971651");
        List<ClNewsAdditional> clNewsAdditionals = crawlerNewsAdditionalService.queryList(clNewsAdditional);
        System.out.println(clNewsAdditionals);
    }

    @Test
    public void testCheckExist(){
        boolean b = crawlerNewsAdditionalService.checkExist("https://blog.csdn.net/weixin_43976602/article/details/96971651");
        System.out.println(b);
    }
}