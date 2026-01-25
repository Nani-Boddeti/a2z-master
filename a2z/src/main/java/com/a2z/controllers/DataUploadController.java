package com.a2z.controllers;

import java.util.List;

import com.a2z.services.interfaces.DataUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.a2z.dao.CategoryData;
import com.a2z.dao.CategoryListData;
import com.a2z.data.UserGroupData;
import com.a2z.data.UserGroupListData;
import com.a2z.services.impl.DefaultDataUploadService;

import jakarta.validation.Valid;

@RestController
@ResponseBody
@RequestMapping("/upload")
@Validated
@Secured({"SCOPE_app.admin","SCOPE_app.read","SCOPE_app.write"})
public class DataUploadController extends RootController {

	@Autowired
	DataUploadService dataUploadService;

	@PostMapping("/categories")
	public List<CategoryData> createCategories(@RequestBody @Valid CategoryListData categories) throws Exception {
		return dataUploadService.createCategory(categories);
	}

	@PutMapping("/categories")
	public List<CategoryData> updateCategories(@RequestBody @Valid CategoryListData categories) {
		return dataUploadService.updateCategories(categories);
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
