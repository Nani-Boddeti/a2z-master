package com.a2z.search.controller;

import java.util.List;

import com.a2z.data.PagedAdPostResult;
import org.apache.commons.lang3.ObjectUtils;
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
	public static final Double DEFAULT_RADIUS_IN_KMS = 0.5;
	@Autowired
	SearchUtil searchUtil;
	
	@GetMapping
	public PagedAdPostResult getSearchResults(@RequestParam String query , @RequestParam(required = false) Double latitude,
											  @RequestParam(required = false) Double longitude , @RequestParam(required = false) Double radius ,
											  @RequestParam(required = false) Integer pageNo,@RequestParam(required = false) Integer pageSize){
		//searchUtil.findByName(query, pageNo, pageSize);
		if(ObjectUtils.isEmpty(radius)){
			radius = DEFAULT_RADIUS_IN_KMS;
		}
		return searchUtil.findByNameAndCatAndLatLong(query, latitude, longitude, radius,pageNo, pageSize);
		//return searchUtil.findByCategoryCodeOrProductName(query,pageNo, pageSize);
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
