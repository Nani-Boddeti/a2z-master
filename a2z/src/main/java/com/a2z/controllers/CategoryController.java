package com.a2z.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.a2z.dao.CategoryData;
import com.a2z.persistence.impl.DefaultCategoryService;

import jakarta.validation.Valid;

@RestController
@ResponseBody
@RequestMapping("/c")
@Validated
public class CategoryController extends RootController {
	
	@Autowired
	DefaultCategoryService categoryService;
	
	@GetMapping("/all/{id}")
	public CategoryData getCategories(@PathVariable @Valid Long id) {
		return categoryService.getCategoryByCode(id);
	}
}
