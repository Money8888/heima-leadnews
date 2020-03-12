package com.heima.crawler.service.impl;

import com.heima.crawler.service.CrawlerNewsCommentService;
import com.heima.model.crawler.pojos.ClNewsComment;
import com.heima.model.mappers.crawerls.ClNewsCommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("all")
public class ClNewsCommentServiceImpl implements CrawlerNewsCommentService {
    @Autowired
    private ClNewsCommentMapper clNewsCommentMapper;

    @Override
    public void saveClNewsComment(ClNewsComment clNewsComment) {
        clNewsCommentMapper.insertSelective(clNewsComment);
    }
}