package com.a2z.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.a2z.dao.CategoryData;
import com.a2z.dao.CategoryListData;
import com.a2z.data.UserGroupData;
import com.a2z.data.UserGroupListData;
import com.a2z.persistence.impl.DefaultDataUploadService;

import jakarta.validation.Valid;

@RestController
@ResponseBody
@RequestMapping("/upload")
@Validated
public class DataUploadController extends RootController {

	@Autowired
	DefaultDataUploadService dataUploadService;

	@PostMapping("/categories")
	public List<CategoryData> createCategories(@RequestBody @Valid CategoryListData categories) {
		return dataUploadService.createCategory(categories);
	}
	
	@PostMapping("/usergroups")
	public List<UserGroupData> createUsergroups(@RequestBody @Valid UserGroupListData userGroups){
		
		return dataUploadService.saveUserGroups(userGroups);
		
	}
	
	@GetMapping("/usergroups/{groupId}")
	public UserGroupData getUserGroup(@PathVariable @Valid Long groupId){
		return dataUploadService.getUserGroup(groupId);
	}
}
