package com.a2z.persistence.impl;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a2z.dao.A2zOrder;
import com.a2z.dao.AdPost;
import com.a2z.dao.ApprovalRequest;
import com.a2z.dao.ApprovalStatus;
import com.a2z.dao.Customer;
import com.a2z.dao.OrderStatus;
import com.a2z.data.OrderData;
import com.a2z.persistence.A2zAdPostRepository;
import com.a2z.persistence.OrderEntryRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.populators.OrderPopulator;
import com.a2z.populators.reverse.AddressReversePopulator;
import com.a2z.populators.reverse.OrderEntryReversePopulator;
import com.a2z.populators.reverse.OrderReversePopulator;
import com.a2z.populators.reverse.PaymentInfoReversePopulator;

@Service
public class DefaultOrderService {

	@Autowired
	OrderPopulator orderPopulator;
	
	@Autowired
	AddressReversePopulator addressReversePopulator;
	
	@Autowired
	RootRepository rootRepo;
	
	@Autowired
	OrderEntryReversePopulator orderEntryReversePopulator;
	
	@Autowired
	OrderEntryRepository orderEntryRepo;
	
	@Autowired
	PaymentInfoReversePopulator paymentInfoReversePopulator;
	
	@Autowired
	OrderReversePopulator orderReversePopulator;
	
	@Autowired
	PODCustomerRepository customerRepo;
	
	@Autowired
	A2zAdPostRepository adPostRepository;
	
	
	public OrderData submitOrder(OrderData orderData , String userName ,boolean isExtended , A2zOrder originalOrder) {
		if (isCustomerEligibleToOrder(userName)) {
			A2zOrder order = submitOrderInternal(orderData, userName, isExtended, originalOrder);
			orderPopulator.populate(order, orderData);
		} else {
			orderData.setErrorMessage("Customer is not eligible to order.");
		}
		return orderData;
	}
	
	private A2zOrder submitOrderInternal(OrderData orderData , String userName ,boolean isExtended, A2zOrder originalOrder) {
		orderData.getCustomer().setUserName(userName);
		A2zOrder order = new A2zOrder();
		orderReversePopulator.populate(orderData, order);
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
	
	public OrderData getOrderDetail(String userName , Long id){
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
	
	public OrderData returnOrExtend(String userName , Long id , boolean isReturned, boolean isExtend){
		OrderData orderData = new OrderData();
		if(StringUtils.isNotEmpty(userName)) {
			Optional<Customer> customerOpt = customerRepo.findById(userName);
			if(customerOpt.isPresent()) {
				Optional<A2zOrder> orderOpt =  rootRepo.getOrderDetails(id, customerOpt.get());
				if(orderOpt.isPresent()) {
					A2zOrder order = orderOpt.get();
					orderPopulator.populate(order, orderData);	
						if(isReturned) {
							order.setStatus(OrderStatus.RETURNED);
							orderData.setStatus(OrderStatus.RETURNED.toString());
							rootRepo.save(order);
						} else {
							submitOrderInternal(orderData, userName, isExtend, order);
						}
				}
			}	
		}
		return orderData;
	}
	
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
}
