package com.heima.model.mappers.app;

import com.heima.model.article.pojos.ApAuthor;

public interface ApAuthorMapper {
    ApAuthor selectById(Integer id);
    ApAuthor selectByAuthorName(String authorName);
    void insert(ApAuthor apAuthor);
}
