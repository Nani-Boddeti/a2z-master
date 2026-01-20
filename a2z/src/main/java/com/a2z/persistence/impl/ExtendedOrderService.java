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

	public A2zAddress saveDeliveryAddress(OrderData orderData) {
		Optional<OrderEntryData> entryDataOpt = orderData.getEntries().stream().findFirst();
		A2zAddress target = new A2zAddress();
		if(entryDataOpt.isPresent()) {
			Optional<AdPost> adPostOpt = adPostRepo.findById(entryDataOpt.get().getAdPost().getId());
			if(adPostOpt.isPresent()) {
				cloneAddressModel(adPostOpt.get().getSourceAddress(),target);
			}
		}
		return saveAddress(target, true , false);
		
	}
	
	public A2zAddress savePaymentAddress(OrderData orderData) {
		Optional<OrderEntryData> entryDataOpt = orderData.getEntries().stream().findFirst();
		A2zAddress target = new A2zAddress();
		if(entryDataOpt.isPresent()) {
			Optional<AdPost> adPostOpt = adPostRepo.findById(entryDataOpt.get().getAdPost().getId());
			if(adPostOpt.isPresent()) {
				cloneAddressModel(adPostOpt.get().getSourceAddress(),target);
			}
		}
		return saveAddress(target, false , true);
	}

	private A2zAddress saveAddress(A2zAddress address,boolean isDelivery , boolean isPayment) {
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
		if(paymentInfoData != null) {
			paymentInfo.setPaymentCode(paymentInfoData.getPaymentCode());
			paymentInfo.setPaymentType(paymentInfoData.getPaymentType());
			paymentInfo.setPaymentAddress(savePaymentAddress(orderData));
			rootRepo.save(paymentInfo);
		}
		return paymentInfo;
	}
	
	private A2zAddress cloneAddressModel(A2zAddress source , A2zAddress target) {
		target.setAppartment(source.getAppartment());
		target.setBuilding(source.getBuilding());
		target.setCellphone(source.getCellphone());
		target.setCompany(source.getCellphone());	
		target.setCustomer(source.getCustomer());
		target.setDistrict(source.getDistrict());
		target.setEmail(source.getEmail());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setLine1(source.getLine1());
		target.setLine2(source.getLine2());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		return target;
	}
}
