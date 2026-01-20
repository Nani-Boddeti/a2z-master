package com.a2z.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.a2z.data.AdPostData;
import com.a2z.search.dao.AdPostSearch;
import com.a2z.search.service.SearchUtil;

@RestController
@ResponseBody
public class SearchController {
	
	@Autowired
	SearchUtil searchUtil;
	
	@GetMapping("/search/{name}")
	public Page<AdPostSearch> getSearchResults(@PathVariable String name , @RequestParam double lat, @RequestParam double lon , @RequestParam double rad){
		searchUtil.findByName(name);
		searchUtil.findByNameAndCatAndLatLong(name, lat, lon, rad);
		return searchUtil.findByCategoryCodeOrProductName(name);
	}
	
	@GetMapping("/search/all")
	public List<AdPostData> getAllPosts(){
		return searchUtil.getAllPosts();
	}
}
