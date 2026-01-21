package com.a2z.services.impl;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.a2z.dao.*;
import com.a2z.data.OrderEntryData;
import com.a2z.services.interfaces.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a2z.data.OrderData;
import com.a2z.persistence.A2zAdPostRepository;
import com.a2z.persistence.OrderEntryRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.populators.OrderPopulator;
import com.a2z.populators.reverse.AddressReversePopulator;
import com.a2z.populators.reverse.OrderEntryReversePopulator;


@Service
public class DefaultOrderService implements OrderService {

	@Autowired
	A2zAdPostRepository adPostRepo;

	@Autowired
	OrderPopulator orderPopulator;

	
	@Autowired
	RootRepository rootRepo;
	
	@Autowired
	OrderEntryReversePopulator orderEntryReversePopulator;
	
	@Autowired
	OrderEntryRepository orderEntryRepo;
	
	/*@Autowired
	PaymentInfoReversePopulator paymentInfoReversePopulator;*/
	
	@Autowired
	PODCustomerRepository customerRepo;
	
	@Autowired
	A2zAdPostRepository adPostRepository;

	@Override
	public void submitOrder(A2zOrder order, boolean isExtended, A2zOrder originalOrder) {

			submitOrderInternal(order, isExtended, originalOrder);


	}
	private A2zOrder submitOrderInternal(A2zOrder order,boolean isExtended, A2zOrder originalOrder) {

		rootRepo.save(order);
		ApprovalRequest approvalRequest = new ApprovalRequest();
		order.getEntries().stream().forEach(entry->{
			AdPost adPost = entry.getAdPost();
			adPost.setActive(false);
			adPost.setModifiedTime(new Date());
			adPostRepository.save(adPost);
			approvalRequest.setAdPost(adPost);
			approvalRequest.setCustomer(entry.getAdPost().getCustomer());
			entry.setOrder(order);
			orderEntryRepo.save(entry);
		});
		if(isExtended && Objects.nonNull(originalOrder)) {
			order.setStatus(OrderStatus.EXTENDED);
			order.setOriginalVersion(originalOrder);
		}
		approvalRequest.setApprovalStatus(ApprovalStatus.IN_REVIEW);
		approvalRequest.setOrder(order);
		rootRepo.save(approvalRequest);
		order.setApprovalRequest(approvalRequest);
		rootRepo.save(order);
		return order;
	}

	@Override
	public List<OrderData> getAllOrders(String userName){
		List<OrderData> orderDataList = new ArrayList<OrderData>();
		if(StringUtils.isNotEmpty(userName)) {
			Optional<Customer> customerOpt = customerRepo.findById(userName);
			if(customerOpt.isPresent()) {
				List<A2zOrder> orderList =  rootRepo.getMyOrders(customerOpt.get());
				orderList.stream().forEach(order->{
					OrderData orderData = new OrderData();
					orderPopulator.populate(order, orderData);
					orderDataList.add(orderData);
				});
			}
		}
		return orderDataList;
	}
	@Override
	public OrderData getOrderDetail(String userName, Long id){
		OrderData orderData = new OrderData();
		if(StringUtils.isNotEmpty(userName)) {
			Optional<Customer> customerOpt = customerRepo.findById(userName);
			if(customerOpt.isPresent()) {
				Optional<A2zOrder> orderOpt =  rootRepo.getOrderDetails(id, customerOpt.get());
				if(orderOpt.isPresent()) {
					orderPopulator.populate(orderOpt.get(), orderData);	
				}
			}
		}
		return orderData;
	}

	@Override
	public boolean isCustomerEligibleToOrder(String userName) {
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		if(customerOpt.isPresent()) {
			Customer customer = customerOpt.get();
		List<A2zOrder> orderList = rootRepo.getOrdersByCustomerAndNotInStatus(OrderStatus.COMPLETED, customer);
		boolean isPrimeCustomer = customer.getUserGroups().stream().anyMatch(ug->ug.getUserGroupName().equalsIgnoreCase("Prime"));
		boolean isBasicCustomer =  customer.getUserGroups().stream().anyMatch(ug->ug.getUserGroupName().equalsIgnoreCase("Basic"));
		return isPrimeCustomer ? true : (isBasicCustomer && orderList.isEmpty() ? true : false);
		}
		return false;	
	}

	@Override
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
}
