package com.heima.model.mappers.app;

import com.heima.model.behavior.pojos.ApShowBehavior;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApShowBehaviorMapper {
    List<ApShowBehavior> selectListByEntryIdAndArticleIds(@Param("entryId")Integer entryId, @Param("articleIds")Integer[] articleIds);

    void saveShowBehavior(@Param("articleIds")Integer[] articleIds, @Param("entryId")Integer id);
}
