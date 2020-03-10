package com.heima.service;

import com.heima.admin.AdminJarApplication;
import com.heima.admin.service.ReviewMediaArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = AdminJarApplication.class)
@RunWith(SpringRunner.class)
public class testReview {

    @Autowired
    private ReviewMediaArticleService reviewMediaArticleService;

    @Test
    public void testReviewArticle(){
        reviewMediaArticleService.autoReviewArticleByMedia(5102);
    }
}
