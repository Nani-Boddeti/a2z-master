package com.a2z.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a2z.dao.A2zAddress;
import com.a2z.dao.AdPost;
import com.a2z.dao.OrderEntry;
import com.a2z.dao.PaymentInfo;
import com.a2z.data.AddressData;
import com.a2z.data.OrderData;
import com.a2z.data.OrderEntryData;
import com.a2z.data.PaymentInfoData;
import com.a2z.persistence.A2zAdPostRepository;
import com.a2z.persistence.OrderEntryRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.populators.reverse.AddressReversePopulator;
import com.a2z.populators.reverse.OrderEntryReversePopulator;
import com.a2z.populators.reverse.PaymentInfoReversePopulator;

@Service
public class ExtendedOrderService {

	
	@Autowired
	AddressReversePopulator addressReversePopulator;
	
	@Autowired
	RootRepository rootRepo;
	
	@Autowired
	OrderEntryReversePopulator orderEntryReversePopulator;
	
	@Autowired
	OrderEntryRepository orderEntryRepo;
		
	@Autowired
	A2zAdPostRepository adPostRepo;

	public A2zAddress saveDeliveryAddress(AddressData addressData) {
		return saveAddress(addressData, true , false);
		
	}
	
	public A2zAddress savePaymentAddress(AddressData addressData) {
		return saveAddress(addressData, false , true);
	}

	private A2zAddress saveAddress(AddressData addressData,boolean isDelivery , boolean isPayment) {
		A2zAddress address = new A2zAddress();
		addressReversePopulator.populate(addressData, address);	
		address.setDeliveryAddress(isDelivery);
		address.setPaymentAddress(isPayment);
		rootRepo.save(address);
		return address;
	}
	
	public List<OrderEntry> saveEntries(OrderData orderData) {
		List<OrderEntry> orderEntryList = new ArrayList<OrderEntry>();
		int count = 0;
		for(OrderEntryData orderEntryData : orderData.getEntries()) {
			OrderEntry orderEntry = new OrderEntry();
			orderEntryReversePopulator.populate(orderEntryData, orderEntry);
			/* orderEntry.setCode((String.valueOf(count))); */
			Optional<AdPost> adPostOpt = adPostRepo.findById(orderEntryData.getAdPost().getId());
			if(adPostOpt.isPresent()) {
				AdPost adPost = adPostOpt.get();
				orderEntry.setAdPost(adPost);
				adPost.setOrderEntry(orderEntry);
				orderEntryRepo.save(orderEntry);
				adPostRepo.save(adPost);
			}
			orderEntryList.add(orderEntry);
			count++;
		}
		return orderEntryList;
	}
	
	public PaymentInfo savePaymentInfo(OrderData orderData) {
		PaymentInfo paymentInfo = new PaymentInfo();
		PaymentInfoData paymentInfoData = orderData.getPaymentInfo();
		paymentInfo.setPaymentCode(paymentInfoData.getPaymentCode());
		paymentInfo.setPaymentType(paymentInfoData.getPaymentType());
		paymentInfo.setPaymentAddress(savePaymentAddress(orderData.getPaymentAddress()));
		rootRepo.save(paymentInfo);
		return paymentInfo;
	}
}
