package com.heima.model.mappers.wemedia;

import java.util.Map;

public interface WmNewsMaterialMapper {
	int countByMid(Integer mid);
	int delByNewsId(Integer nid);
	void saveRelationsByContent(Map<String, Object> materials, Integer newsId, Short type);
}