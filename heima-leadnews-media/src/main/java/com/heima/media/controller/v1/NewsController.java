package com.heima.media.controller.v1;

import com.heima.common.media.constans.WmMediaConstans;
import com.heima.media.apis.NewsControllerApi;
import com.heima.media.service.NewsService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.media.dtos.WmNewsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/media/news")
public class NewsController implements NewsControllerApi {

	@Autowired
	private NewsService newsService;

	@PostMapping("/submit")
	@Override  
	public ResponseResult summitNews(@RequestBody WmNewsDto wmNews) {
		return newsService.saveNews(wmNews, WmMediaConstans.WM_NEWS_SUMMIT_STATUS);
	}  


	@PostMapping("/save_draft")  
	@Override  
	public ResponseResult saveDraftNews(@RequestBody WmNewsDto wmNews) {  
		return newsService.saveNews(wmNews, WmMediaConstans.WM_NEWS_DRAFT_STATUS);  
	}

}