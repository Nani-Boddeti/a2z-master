package com.a2z.search.controller;

import java.util.List;

import com.a2z.data.PagedAdPostResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.a2z.data.AdPostData;
import com.a2z.search.dao.AdPostSearch;
import com.a2z.search.service.SearchUtil;

@RestController
@ResponseBody
@RequestMapping("/search/")
public class SearchController {
	
	@Autowired
	SearchUtil searchUtil;
	
	@GetMapping
	public PagedAdPostResult getSearchResults(@RequestParam String query , @RequestParam(required = false) Double lat,
											  @RequestParam(required = false) Double lon , @RequestParam(required = false) Double rad ,
											  @RequestParam(required = false) Integer pageNo,@RequestParam(required = false) Integer pageSize){
		searchUtil.findByName(query, pageNo, pageSize);
		searchUtil.findByNameAndCatAndLatLong(query, lat, lon, rad,pageNo, pageSize);
		return searchUtil.findByCategoryCodeOrProductName(query,pageNo, pageSize);
	}
	
	@GetMapping("all")
	public PagedAdPostResult getAllPosts(@RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer pageSize){
		return searchUtil.getAllPosts(pageNo, pageSize);
	}

	@GetMapping("category/{code}")
	public PagedAdPostResult getPostsByCategory(@PathVariable String code,@RequestParam Integer pageNo,@RequestParam Integer pageSize){
		return searchUtil.findByCategoryCode(code, pageNo, pageSize);

	}
}
