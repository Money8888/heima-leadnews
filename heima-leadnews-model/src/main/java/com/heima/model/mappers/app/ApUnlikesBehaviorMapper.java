package com.heima.model.mappers.app;

import com.heima.model.behavior.pojos.ApUnlikesBehavior;
import org.apache.ibatis.annotations.Param;

public interface ApUnlikesBehaviorMapper {
    ApUnlikesBehavior selectLastUnLike(@Param("entryId") Integer entryId, @Param("articleId") Integer articleId);
    int insert(ApUnlikesBehavior apUnlikesBehavior);
}
