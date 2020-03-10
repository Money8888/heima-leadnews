package com.heima.media.apis;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.dtos.WmNewsDto;
import com.heima.model.media.dtos.WmNewsPageReqDto;

public interface NewsControllerApi {

   /**  
    * 提交文章*  
    * @param wmNews*  
    * @return*  
    */  
   ResponseResult summitNews(WmNewsDto wmNews);

    /**  
	* 保存草稿
	* @param wmNews
	* @return
	*/ 
    ResponseResult saveDraftNews(WmNewsDto wmNews);

    /**
     * 用户查询文章列表
     * @return
     */
    ResponseResult listByUser(WmNewsPageReqDto dto);

    /**
     * 根据id获取文章信息
     * @param dto
     * @return
     */
    ResponseResult wmNews(WmNewsDto dto);

    /**
     * 删除文章
     * @param dto
     * @return
     */
    ResponseResult delNews(WmNewsDto dto);

}