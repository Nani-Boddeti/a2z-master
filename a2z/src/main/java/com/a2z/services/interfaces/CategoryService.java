package com.a2z.services.interfaces;

import com.a2z.dao.A2zCategory;
import com.a2z.dao.CategoryData;
import com.a2z.dao.CategoryListData;

import java.util.List;

public interface CategoryService {
    List<CategoryData> createCategory(CategoryListData categories);

    List<CategoryData> getAllCategories();

    CategoryData getCategoryDataByCode(String code);

    A2zCategory getCategoryByCode(String code);

    List<String> getAvailableCategoryNames();
}
