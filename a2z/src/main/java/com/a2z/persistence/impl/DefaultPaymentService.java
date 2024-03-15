package com.a2z.persistence.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a2z.dao.Customer;
import com.a2z.dao.PrimeStatus;
import com.a2z.dao.PrimeUser;
import com.a2z.dao.UserGroup;
import com.a2z.data.CustomerData;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.RootRepository;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;

@Service
public class DefaultPaymentService {

	static int DAYS_OFFSET = 30;
	
	@Autowired
	RootRepository rootRepository;
	
	@Autowired
	PODCustomerRepository customerRepo;
	
	private void setPrimeGroup(Customer customer) {
		List<UserGroup> userGroups = new ArrayList<UserGroup>();
		if(CollectionUtils.isNotEmpty(customer.getUserGroups()))
			userGroups.addAll(customer.getUserGroups());
		Optional<UserGroup> userGroupOpt = rootRepository.getUserGroupByName("Prime");
		if(userGroupOpt.isPresent() && !userGroups.contains(userGroupOpt.get())) {
			userGroups.add(userGroupOpt.get());
		}
		customer.setUserGroups(userGroups);
		customerRepo.save(customer);
	}
	
	private void createPrimeUser(Customer customer , UserGroup primeUserGroup) {
		PrimeUser primeUser = new PrimeUser();
		primeUser.setCustomer(customer);
		primeUser.setIsActive(true);
		primeUser.setPrimeAmount(null);
		List<PrimeUser> primeUserList = rootRepository.getPrimeUserByCustomer(customer);
		List<PrimeUser> primeUserRenewedList = rootRepository.getPrimeUserByCustomerAndStatus(PrimeStatus.RENEWED, customer);
		primeUser.setStatus(PrimeStatus.SUBSCRIBED);
		if(CollectionUtils.isNotEmpty(primeUserList))
		primeUser.setStatus(PrimeStatus.RENEWED);
		else if (CollectionUtils.isNotEmpty(primeUserRenewedList)) {
			primeUser.setStatus(PrimeStatus.SYSTEM_RENEWED);
		}
		LocalDate currentDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		primeUser.setPrimedDate(currentDate);
		primeUser.setPrimeGroup(primeUserGroup);
		 LocalDate expiredDate = currentDate.plusDays(DAYS_OFFSET);
		primeUser.setPrimeExpireDate(expiredDate);
		rootRepository.save(primeUser);
	}
}
