package com.heima.media.apis;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.dtos.WmNewsDto;

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

}