package com.a2z.services.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.a2z.dao.*;
import com.a2z.data.OrderData;
import com.a2z.data.OrderEntryData;
import com.a2z.data.PaymentInfoData;
import com.a2z.enums.PrimeStatus;
import com.a2z.persistence.A2zAdPostRepository;
import com.a2z.services.interfaces.AddressService;
import com.a2z.services.interfaces.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.RootRepository;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;

@Service
public class DefaultPaymentService implements PaymentService {

	static int DAYS_OFFSET = 30;
	
	@Autowired
	RootRepository rootRepository;
	
	@Autowired
	PODCustomerRepository customerRepo;

	@Autowired
	A2zAdPostRepository adPostRepo;

	@Autowired
	AddressService addressService;

	@Override
	public void setPrimeGroup(Customer customer) {
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

	@Override
	public void createPrimeUser(Customer customer, UserGroup primeUserGroup) {
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

	@Override
	public A2zAddress savePaymentAddress(OrderData orderData) {
		Optional<OrderEntryData> entryDataOpt = orderData.getEntries().stream().findFirst();
		A2zAddress target = new A2zAddress();
		if(entryDataOpt.isPresent()) {
			Optional<AdPost> adPostOpt = adPostRepo.findById(entryDataOpt.get().getAdPost().getId());
			if(adPostOpt.isPresent()) {
				addressService.cloneAddressModel(adPostOpt.get().getSourceAddress(),target);
			}
		}
		return addressService.saveAddress(target, false , true);
	}

	@Override
	public PaymentInfo savePaymentInfo(OrderData orderData) {
		PaymentInfo paymentInfo = new PaymentInfo();
		PaymentInfoData paymentInfoData = orderData.getPaymentInfo();
		if(paymentInfoData != null) {
			paymentInfo.setPaymentCode(paymentInfoData.getPaymentCode());
			paymentInfo.setPaymentType(paymentInfoData.getPaymentType());
			paymentInfo.setPaymentAddress(this.savePaymentAddress(orderData));
			rootRepository.save(paymentInfo);
		}
		return paymentInfo;
	}
}
