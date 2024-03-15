package com.a2z.persistence.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a2z.dao.A2zCategory;
import com.a2z.dao.CategoryData;
import com.a2z.dao.CategoryListData;
import com.a2z.persistence.RootRepository;


@Service
public class DefaultCategoryService {
	@Autowired
	RootRepository rootRepo;
	
	public List<CategoryData> createCategory(CategoryListData categories) {
				return categories.getCategories().stream().map(this::saveCategory).collect(Collectors.toUnmodifiableList());
	}
	
	private CategoryData saveCategory(CategoryData categoryData) {
		A2zCategory category = new A2zCategory();
		category.setCategoryCode(categoryData.getCategoryCode());
		category.setIsVisible(categoryData.getIsVisible());
		rootRepo.save(category);		
		return categoryData;
	}
	
	public List<CategoryData> getAllCategories(){
		List<A2zCategory> cateogriesList = rootRepo.getAllCategories(true);
		return cateogriesList.stream().map(this::convertCategory).collect(Collectors.toUnmodifiableList());
	}
	
	private CategoryData convertCategory(A2zCategory category) {
		CategoryData catData = new CategoryData();
		catData.setCategoryCode(category.getCategoryCode());
		catData.setIsVisible(category.getIsVisible());
		return  catData;
	}
	
	public CategoryData getCategoryByCode(Long id) {
		Optional<A2zCategory> cat = rootRepo.getCategory(id);
		CategoryData catData = new CategoryData();
		if(cat.isPresent()) {
			A2zCategory category = cat.get();
			catData.setCategoryCode(category.getCategoryCode());
			catData.setIsVisible(category.getIsVisible());
		}
		return catData;
	}
}

