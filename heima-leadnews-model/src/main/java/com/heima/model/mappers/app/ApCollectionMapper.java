package com.heima.model.mappers.app;

import com.heima.model.article.pojos.ApCollection;
import org.apache.ibatis.annotations.Param;

/**
 * 文章收藏
 */
public interface ApCollectionMapper {
    /**
     * @param burst mycat字段
     * @param objectId 行为实体id
     * @param entryId 文章id
     * @param type 收藏类型，文章还是动态
     * @return
     */
    ApCollection selectForEntryId(@Param("burst") String burst, @Param("objectId") Integer objectId, @Param("entryId") Integer entryId, @Param("type") Short type);
}
