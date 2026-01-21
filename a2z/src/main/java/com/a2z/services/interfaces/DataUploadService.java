package com.a2z.services.interfaces;

import com.a2z.dao.CategoryData;
import com.a2z.dao.CategoryListData;
import com.a2z.data.UserGroupData;
import com.a2z.data.UserGroupListData;

import java.util.List;

public interface DataUploadService {
    List<CategoryData> createCategory(CategoryListData categories) throws Exception;

    List<CategoryData> updateCategories(CategoryListData categories);

    List<UserGroupData> saveUserGroups(UserGroupListData userGroups);

    UserGroupData getUserGroup(Long id);
}
