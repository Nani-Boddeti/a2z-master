package com.a2z.services.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.a2z.services.interfaces.CategoryService;
import com.a2z.populators.CategoryPopulator;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a2z.dao.A2zCategory;
import com.a2z.dao.CategoryData;
import com.a2z.dao.CategoryListData;
import com.a2z.persistence.RootRepository;


@Service
public class DefaultCategoryService implements CategoryService {
	@Autowired
	RootRepository rootRepo;

	@Autowired
	CategoryPopulator categoryPopulator;

	@Override
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

	@Override
	public List<CategoryData> getAllCategories(){
		List<A2zCategory> cateogriesList = rootRepo.getAllCategories(true);
		return Stream.ofNullable(cateogriesList).flatMap(Collection::stream)
				.map(this::convertCategory).collect(Collectors.toList());
	}
	
	private CategoryData convertCategory(A2zCategory category) {
		CategoryData catData = new CategoryData();
		if(ObjectUtils.isNotEmpty(category)) {
			categoryPopulator.populate(category,catData);
		}
		return  catData;
	}

	@Override
	public CategoryData getCategoryDataByCode(String code) {
		Optional<A2zCategory> cat = rootRepo.getCategoryByCode(code);
		CategoryData catData = new CategoryData();
		if(cat.isPresent()) {
			categoryPopulator.populate(cat.get(),catData);
		}
		return catData;
	}

	@Override
	public List<String> getAvailableCategoryNames() {
		List<String> catNameList = rootRepo.getAllCategorieNames(true);
		return Stream.ofNullable(catNameList).flatMap(Collection::stream)
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public A2zCategory getCategoryByCode(String code) {
		Optional<A2zCategory> cat = rootRepo.getCategoryByCode(code);
		if(cat.isPresent()) {
			return cat.get();}
		return null;
	}
}

