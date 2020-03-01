package com.heima.model.mappers.app;

import com.heima.model.behavior.pojos.ApLikesBehavior;
import org.apache.ibatis.annotations.Param;

public interface ApLikesBehaviorMapper {
    ApLikesBehavior selectLastLike(@Param("burst") String burst, @Param("objectId") Integer objectId, @Param("entryId") Integer entryId, @Param("type") Short type);
    int insert(ApLikesBehavior apLikesBehavior);
}
