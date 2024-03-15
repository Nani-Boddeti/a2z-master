package com.a2z.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
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
public class DefaultDataUploadService {

	@Autowired
	RootRepository rootRepo;
	
	@Autowired
	PODCustomerRepository customerRepo;
	
	@Autowired
	CustomerPopulator customerPopulator;
	
	public List<CategoryData> createCategory(CategoryListData categories) {
				return categories.getCategories().stream().map(this::saveCategory).collect(Collectors.toUnmodifiableList());
	}
	
	private CategoryData saveCategory(CategoryData categoryData) {
		A2zCategory category = new A2zCategory();
		category.setCategoryCode(categoryData.getCategoryCode());
		category.setIsVisible(BooleanUtils.isTrue(categoryData.getIsVisible()));
		rootRepo.save(category);		
		return categoryData;
	}
	
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
