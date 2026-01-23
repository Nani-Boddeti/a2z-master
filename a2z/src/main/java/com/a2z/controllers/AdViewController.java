package com.a2z.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.a2z.services.interfaces.AdPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.a2z.data.AdPostData;
import com.a2z.services.impl.DefaultAdPostService;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@ResponseBody
@RequestMapping("/ad")
@Validated
public class AdViewController extends RootController {
	
	@Autowired
	AdPostService adPostService;

	@GetMapping("/all")
	public List<AdPostData> viewAllAds( HttpServletRequest request){
		String userName = getSessionUserName();
		List<AdPostData> adsList = new ArrayList<AdPostData>();
		if(StringUtils.isNotEmpty(userName)) {
			adsList = adPostService.retriveAllAds();
		}
		return Collections.unmodifiableList(adsList);
	}
	
	@GetMapping("/view/{id}")
	public AdPostData viewAd(@PathVariable @Valid final Long id, HttpServletRequest request){
		String userName = getSessionUserName();
		AdPostData ad = new AdPostData();
		if(StringUtils.isNotEmpty(userName)) {
			ad = adPostService.getAdById(id);
		}
		return ad;
	}
	
	@PostMapping("/post")
	public AdPostData submitAdPost(@RequestBody @Valid AdPostData adPostData,HttpServletRequest request) {
		String userName = getSessionUserName();		
		return adPostService.submitAdPost(adPostData,userName );
	}
	
	@GetMapping("/activate/{id}")
	public AdPostData reActivatePost(@PathVariable @Valid final Long id,HttpServletRequest request) {
		return adPostService.activatePost(id);
	}
	
//	@GetMapping("/return/{id}")
//	public AdPostData returnStatus(@PathVariable @Valid final Long id, @RequestParam(required = true , defaultValue = "false" )@Valid  boolean isReceived , @RequestParam(required = true , defaultValue = "false" )@Valid boolean reactivate ,   HttpServletRequest request) {
//		return adPostService.receivedStatusUpdate(id,isReceived,reactivate);
//	}
	
}
