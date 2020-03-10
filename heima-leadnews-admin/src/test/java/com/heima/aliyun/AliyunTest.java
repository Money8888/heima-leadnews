package com.heima.aliyun;

import com.heima.admin.AdminJarApplication;
import com.heima.common.aliyun.AliyunImageScanRequest;
import com.heima.common.aliyun.AliyunTextScanRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = AdminJarApplication.class)
@RunWith(SpringRunner.class)
public class AliyunTest {

    @Autowired
    private AliyunTextScanRequest aliyunTextScanRequest;

    @Autowired
    private AliyunImageScanRequest aliyunImageScanRequest;

    @Test
    public void testText() throws Exception {
        String message = "11111";
        String response = aliyunTextScanRequest.textScanRequest(message);
        System.out.println(response);
    }

    @Test
    public void testImageScanRequest(){
        try {
            List list = new ArrayList<>();
            list.add("http://47.94.7.85/group1/M00/00/00/rBENvl02ZtKAEgFqAACNdiGk7IM981.jpg");
            String response = aliyunImageScanRequest.imageScanRequest(list);
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
