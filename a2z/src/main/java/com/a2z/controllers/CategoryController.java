package com.a2z.controllers;


import com.a2z.enums.AdCategory;
import com.a2z.services.interfaces.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.a2z.dao.CategoryData;

import jakarta.validation.Valid;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@ResponseBody
@RequestMapping("/api/c")
@Validated
public class CategoryController extends RootController {
	
	@Autowired
	CategoryService categoryService;
	
	@GetMapping("/all/{code}")
	public CategoryData getCategories(@PathVariable @Valid String code) {
		return categoryService.getCategoryDataByCode(code);
	}

	@GetMapping("/listed")
	public Iterable<String> getAllCategorieNames() {
		List<String> colorNamesStream = Arrays.stream(AdCategory.values())
				.map(AdCategory::name) // or .map(Enum::name) or .map(Color::toString)
				.collect(Collectors.toList());
		return colorNamesStream;

	}

	@GetMapping("/allCategories")
	public List<CategoryData> getAllCategories() {
		return categoryService.getAllCategories();
	}


}
