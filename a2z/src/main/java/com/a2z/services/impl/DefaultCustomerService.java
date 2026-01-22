package com.a2z.services.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.a2z.dao.*;
import com.a2z.data.*;
import com.a2z.events.CustomerRegistrationEvent;
import com.a2z.populators.reverse.AddressReversePopulator;
import com.a2z.services.interfaces.CustomerService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.a2z.persistence.A2zAdPostRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.PODOTPRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.populators.AdPostPopulator;
import com.a2z.populators.CustomerPopulator;
import com.a2z.populators.OrderPopulator;
import com.a2z.populators.WishlistPopulator;
import com.a2z.populators.reverse.CustomerReversePopulator;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;


@Service
public class DefaultCustomerService implements CustomerService {

	@Value("${otp.validation.time.mins}")
	private int OtpValidTime;
	@Autowired
	private PODCustomerRepository customerRepo;
	
	@Autowired
	private PODOTPRepository otpRepo;
	
	@Autowired
	private CustomerReversePopulator customerReversePopulator;
	
	@Autowired
	private CustomerPopulator customerPopulator;
	
	@Autowired
	private AdPostPopulator  adPostPopulator;
	
	@Autowired
	private RootRepository rootRepository;
	
	@Autowired
	private OrderPopulator orderPopulator;
	
	@Autowired
	private A2zAdPostRepository adPostRepository;
	
	@Autowired
	private WishlistPopulator wishlistPopulator;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	AddressReversePopulator addressReversePopulator;

	@Autowired
	ApplicationEventPublisher eventPublisher;

	private static final int PAGE_SIZE = 10;
	private static final int DEFAULT_PAGE_NO = 0;


	public DefaultCustomerService() {
		
	}
	public DefaultCustomerService(PODCustomerRepository customerRepo) {
		this.customerRepo = customerRepo;
	}
	
	/**
	 * Authenticate a customer by validating username and password
	 *
	 * @param username The username to authenticate
	 * @param password The password to validate
	 * @return Customer object if authentication is successful, null otherwise
	 */
	@Override
	public Customer authenticateCustomer(String username, String password) {
		Optional<Customer> customerOpt = customerRepo.findById(username);

		if (customerOpt.isPresent()) {
			Customer customer = customerOpt.get();
			// Validate password using the password encoder
			if (passwordEncoder.matches(password, customer.getPassword())) {
				return customer;
			}
		}

		return null;
	}

	@Override
	@Transactional
	public CustomerData saveCustomer(CustomerData customerData) {
		String encodedPassword = passwordEncoder.encode(customerData.getPassword());
		Customer savedCustomer = null;
		Optional<Customer> existingCustomer = customerRepo.findById(customerData.getUserName());
		if (existingCustomer.isEmpty()) {
			Customer customer = new Customer(customerData.getUserName(), encodedPassword, customerData.getEmail());
		customerReversePopulator.populate(customerData, customer);
		customer.setRole("ROLE_USER");
		List<UserGroup> userGroups = new ArrayList<UserGroup>();
		if (CollectionUtils.isNotEmpty(customer.getUserGroups()))
			userGroups.addAll(customer.getUserGroups());
		Optional<UserGroup> userGroupOpt = rootRepository.getUserGroupByName("Prime");
		if (userGroupOpt.isPresent() && !userGroups.contains(userGroupOpt.get())) {
			userGroups.add(userGroupOpt.get());
		}
		customer.setUserGroups(userGroups);
		customerRepo.save(customer);

		if (customerData.getDefaultAddress() != null) {
			customerData.getDefaultAddress().setCustomer(customer.getUserName());
			A2zAddress defaultAddress = new A2zAddress();
			addressReversePopulator.populate(customerData.getDefaultAddress(), defaultAddress);
//			defaultAddress.setCustomer(customer);
			rootRepository.save(defaultAddress);
			savedCustomer = customer;
		}
		sendCustomerRegistrationEmail(customer);
	} else{
			savedCustomer = existingCustomer.get();
		}
		CustomerData savedCustomerData = new CustomerData();
		customerPopulator.populate(savedCustomer, savedCustomerData);
		return savedCustomerData;
	}

	@Override
	public void deleteCustomer(Long id) {
		customerRepo.deleteById(null);
	}

	@Override
	public void disableCustomer(String id) {

		Optional<Customer> customer = customerRepo.findById(id);
		if (customer.isPresent()) {
			Customer cust = customer.get();
			cust.setDisabled(true);
			customerRepo.save(cust);
		}
	}

	@Override
	public Customer getUserByEmailORPhone(String phone, String email) {
		return customerRepo.getUserByEmailORPhone(phone, email);
	}

	@Override
	public CustomerData validateOTP(OTPFormData otpFormData) {
		CustomerData customerData = new CustomerData();
		Customer customer = getUserByEmailORPhone(otpFormData.getPhone(),otpFormData.getPhone());
		if(Objects.nonNull(customer)) {
		Optional<OTP> otpOptional = otpRepo.findById(customer.getUserName());
		if(otpOptional.isPresent()) {
			OTP otp = otpOptional.get();
			Date timeCreated = otp.getTimeCreated();
			Date timeCreatedPlusOtpTime = DateUtils.addMinutes(timeCreated, OtpValidTime);
			if(timeCreatedPlusOtpTime.compareTo(new Date())>=0) {
				customerPopulator.populate(customer, customerData);
				otp.setValidated(true);
				otpRepo.save(otp);
			}
		}
		}
		return customerData;
	}

	@Override
	public CustomerData getCustomerProfile(String userName) {
		CustomerData customerData = new CustomerData();
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		if(customerOpt.isPresent()) {
			Customer customer = customerOpt.get();
				customerPopulator.populate(customer, customerData);
		}
		return customerData;
	}

	@Override
	public PagedAdPostResult retriveAllMyAds(String userName, Integer pageNo, Integer pageSize) {
		List<AdPostData> myAdDataList = new ArrayList<AdPostData>();
		int pageNumber = DEFAULT_PAGE_NO;
		int size = PAGE_SIZE;
		if(ObjectUtils.isNotEmpty(pageNo))
		{
			pageNumber = pageNo.intValue()-1;
		}
		if(ObjectUtils.isNotEmpty(pageSize))
		{
			size = pageSize.intValue();
		}
		PageRequest pageRequest = PageRequest.of(pageNumber, size);
		PagedAdPostResult pagedResult = new PagedAdPostResult();

		Optional<Customer> customerOpt = customerRepo.findById(userName);
		if(customerOpt.isPresent()) {

			Customer customer = customerOpt.get();
			Page<AdPost> pagedAdPosts = adPostRepository.findByCustomer(customer, pageRequest);
			pagedResult.setCurrentPage(pagedAdPosts.getPageable().getPageNumber());
			pagedResult.setTotalPages(pagedAdPosts.getTotalPages());
			List<AdPost> myAdList = customer.getAdList();
			myAdList.forEach(ad->{
				AdPostData adData = new AdPostData();
				adPostPopulator.populate(ad, adData);
				myAdDataList.add(adData);
			});
		}
		pagedResult.setAdPosts(myAdDataList);
		return pagedResult;
	}

	@Override
	public PagedA2zOrderResult getAllMyOrders(String userName, Integer pageNo, Integer pageSize){
		List<OrderData> orderDataList = new ArrayList<OrderData>();
		int pageNumber = DEFAULT_PAGE_NO;
		int size = PAGE_SIZE;
		if(ObjectUtils.isNotEmpty(pageNo))
		{
			pageNumber = pageNo.intValue()-1;
		}
		if(ObjectUtils.isNotEmpty(pageSize))
		{
			size = pageSize.intValue();
		}
		PageRequest pageRequest = PageRequest.of(pageNumber, size);
		PagedA2zOrderResult pagedResult = new PagedA2zOrderResult();
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		if(customerOpt.isPresent()) {
			Customer customer = customerOpt.get();			
			Page<A2zOrder> myOrdList = rootRepository.getMyOrders(customer,pageRequest);
			pagedResult.setCurrentPage(myOrdList.getPageable().getPageNumber());
			pagedResult.setTotalPages(myOrdList.getTotalPages());
			myOrdList.forEach(ord->{
				OrderData ordData = new OrderData();
				orderPopulator.populate(ord, ordData);
				orderDataList.add(ordData);
			});
		}
		pagedResult.setA2zOrders(orderDataList);
		return pagedResult;
	}
	@Override
	public OrderData viewOrder(Long id, String userName){
			OrderData orderData = new OrderData();
			Optional<Customer> customerOpt = customerRepo.findById(userName);
			if(customerOpt.isPresent()) {
				Optional<A2zOrder> order = rootRepository.getOrderDetails(id,customerOpt.get());
				if(order.isPresent()) {
					orderPopulator.populate(order.get(), orderData);
				}
			}
			
			
		return orderData;
	}
	@Override
	public PagedA2zApprovalResult getAllApprovalRequests(String userName, Integer pageNo, Integer pageSize){
		int pageNumber = DEFAULT_PAGE_NO;
		int size = PAGE_SIZE;
		if(ObjectUtils.isNotEmpty(pageNo))
		{
			pageNumber = pageNo.intValue()-1;
		}
		if(ObjectUtils.isNotEmpty(pageSize))
		{
			size = pageSize.intValue();
		}
		PageRequest pageRequest = PageRequest.of(pageNumber, size);
		PagedA2zApprovalResult pagedResult = new PagedA2zApprovalResult();
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		List<ApprovalRequestData> approvalRequestDataList = new ArrayList<ApprovalRequestData>();
		if(customerOpt.isPresent()) {
			Page<ApprovalRequest> approvalRequests = rootRepository.getAllApprovalRequests(customerOpt.get(),pageRequest);
			pagedResult.setTotalPages(approvalRequests.getTotalPages());
			pagedResult.setCurrentPage(approvalRequests.getPageable().getPageNumber());
			approvalRequests.stream().forEach(approvalRequest->{
				ApprovalRequestData approvalReqData = new ApprovalRequestData();
				approvalReqData.setId(approvalRequest.getId());
				approvalReqData.setStatus(approvalRequest.getApprovalStatus().toString());
				AdPostData adPostData = new AdPostData();
				adPostPopulator.populate(approvalRequest.getAdPost(), adPostData);
				approvalReqData.setAdPostData(adPostData);
				CustomerData customerData = new CustomerData();
				customerPopulator.populate(approvalRequest.getOrder().getCustomer(), customerData);
				approvalReqData.setCustomerData(customerData);
				approvalRequestDataList.add(approvalReqData);
			});

		}
		pagedResult.setApprovalRequestDataList(approvalRequestDataList);
		return pagedResult;
	}
	@Override
	public ApprovalRequestData getApprovalRequest(Long id, String userName){
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		ApprovalRequestData approvalReqData = new ApprovalRequestData();
		if(customerOpt.isPresent()) {
			Optional<ApprovalRequest> approvalRequestOpt = rootRepository.getApprovalRequestDetails(id,customerOpt.get());
			if(approvalRequestOpt.isPresent()) {
				ApprovalRequest approvalRequest = approvalRequestOpt.get();
					approvalReqData.setId(approvalRequest.getId());
					approvalReqData.setStatus(approvalRequest.getApprovalStatus().toString());
					AdPostData adPostData = new AdPostData();
					adPostPopulator.populate(approvalRequest.getAdPost(), adPostData);

			}	
		}
		
		return approvalReqData;
	}
	@Override
	public ApprovalRequestData submitApprovalRequest(ApprovalRequestPostData requestData, String userName){
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		ApprovalRequestData approvalReqData = new ApprovalRequestData();
		if(customerOpt.isPresent()) {
			Optional<ApprovalRequest> approvalRequestOpt = rootRepository.getApprovalRequestDetails(requestData.getId(),customerOpt.get());
			if(approvalRequestOpt.isPresent()) {
				ApprovalRequest approvalRequest = approvalRequestOpt.get();
				approvalRequest.setApprovalStatus(ApprovalStatus.valueOf(requestData.getStatus()));
				approvalRequest.getOrder().setStatus(OrderStatus.valueOf(requestData.getStatus()));
				rootRepository.save(approvalRequest.getOrder());
				rootRepository.save(approvalRequest);
					approvalReqData.setId(approvalRequest.getId());
					approvalReqData.setStatus(approvalRequest.getApprovalStatus().toString());
					AdPostData adPostData = new AdPostData();
					adPostPopulator.populate(approvalRequest.getAdPost(), adPostData);
			}	
		}
		
		return approvalReqData;
	}

	@Override
	public boolean isCustomerEligibleToPost(String userName) {
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		if(customerOpt.isPresent()) {
			Customer customer = customerOpt.get();
		List<AdPost> adList = adPostRepository.findByActiveAndCustomer(true , customer);
		boolean isPrimeCustomer = customer.getUserGroups().stream().anyMatch(ug->ug.getUserGroupName().equalsIgnoreCase("Prime"));
		boolean isBasicCustomer =  customer.getUserGroups().stream().anyMatch(ug->ug.getUserGroupName().equalsIgnoreCase("Basic"));
		return isPrimeCustomer ? true : (isBasicCustomer && adList.isEmpty() ? true : false);
		}
	return false;	
	}

	private void sendCustomerRegistrationEmail(Customer customer) {
		CustomerRegistrationEvent event = new CustomerRegistrationEvent(customer);
		eventPublisher.publishEvent(event);
	}
	
}