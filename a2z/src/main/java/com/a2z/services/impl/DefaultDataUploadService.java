package com.a2z.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.a2z.services.interfaces.CategoryService;
import com.a2z.services.interfaces.DataUploadService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a2z.dao.A2zCategory;
import com.a2z.dao.CategoryData;
import com.a2z.dao.CategoryListData;
import com.a2z.dao.Customer;
import com.a2z.dao.UserGroup;
import com.a2z.data.UserGroupData;
import com.a2z.data.UserGroupListData;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.populators.CustomerPopulator;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;


@Service
public class DefaultDataUploadService implements DataUploadService {

	@Autowired
	RootRepository rootRepo;
	
	@Autowired
	PODCustomerRepository customerRepo;
	
	@Autowired
	CustomerPopulator customerPopulator;

	@Autowired
	CategoryService categoryService;
	@Override
	public List<CategoryData> createCategory(CategoryListData categories) throws Exception {
		try{
			return categories.getCategories().stream().map(this::saveOrUpdateCategory).collect(Collectors.toUnmodifiableList());
		}
			catch (Exception ex)	{
			throw new Exception("Duplicate Category Code Found", ex);
			}
	}

	@Override
	public List<CategoryData> updateCategories(CategoryListData categories) {
		return categories.getCategories().stream().map(this::saveOrUpdateCategory).collect(Collectors.toUnmodifiableList());
	}

	private CategoryData saveOrUpdateCategory(CategoryData categoryData) {
		A2zCategory category = categoryService.getCategoryByCode(categoryData.getCategoryCode());
		if(ObjectUtils.isNotEmpty(category)){
			category.setCategoryName(categoryData.getCategoryName());
			category.setIsVisible(BooleanUtils.isTrue(categoryData.getIsVisible()));
			rootRepo.save(category);
		} else {
			this.saveCategory(categoryData);
		}
		return categoryData;
	}

	private CategoryData saveCategory(CategoryData categoryData) {
		A2zCategory category = new A2zCategory();
		category.setCategoryCode(categoryData.getCategoryCode());
		category.setCategoryName(categoryData.getCategoryName());
		category.setIsVisible(BooleanUtils.isTrue(categoryData.getIsVisible()));
		rootRepo.save(category);		
		return categoryData;
	}
	@Override
	public List<UserGroupData> saveUserGroups(UserGroupListData userGroups){
		return userGroups.getUserGroups().stream().map(this::saveUserGroup).collect(Collectors.toUnmodifiableList());
	}
	
	private UserGroupData saveUserGroup(UserGroupData userGroupData) {
		UserGroup userGroup = new UserGroup();
		userGroup.setUserGroupName(userGroupData.getUserGroupName());
		List<Customer> customers = new ArrayList<Customer>();
		if (CollectionUtils.isNotEmpty(userGroupData.getUserNames())) {
			userGroupData.getUserNames()
			.forEach(username ->{
				Optional<Customer> customerOpt = customerRepo.findById(username);
				if(customerOpt.isPresent()) {
					Customer customer =  customerOpt.get();
					customers.add(customer);
				}
				
			});
		}
		userGroup.setCustomers(customers);
		rootRepo.save(userGroup);		
		return userGroupData;
	}
	@Override
	public UserGroupData getUserGroup(Long id) {
		Optional<UserGroup> ugOpt = rootRepo.getUserGroup(id);
		UserGroupData ugData = new UserGroupData();
		if(ugOpt.isPresent()) {
			UserGroup ug = ugOpt.get();
			ugData.setUserGroupName(ug.getUserGroupName());
			List<String> customerNameList = ug.getCustomers().stream().map(Customer::getUserName).collect(Collectors.toUnmodifiableList());
			ugData.setUserNames(customerNameList);	
		}
		return ugData;
	}
}
