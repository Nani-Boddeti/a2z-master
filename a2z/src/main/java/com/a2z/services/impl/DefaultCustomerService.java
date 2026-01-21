package com.a2z.services.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.a2z.dao.*;
import com.a2z.populators.reverse.AddressReversePopulator;
import com.a2z.services.interfaces.CustomerService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.a2z.data.AdPostData;
import com.a2z.data.ApprovalRequestData;
import com.a2z.data.ApprovalRequestPostData;
import com.a2z.data.CustomerData;
import com.a2z.data.OTPFormData;
import com.a2z.data.OrderData;
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
	public CustomerData saveCustomer(CustomerData customerData) {
		String encodedPassword = passwordEncoder.encode(customerData.getPassword());
		Customer customer = new Customer(customerData.getUserName(),encodedPassword,customerData.getEmail());
		customerReversePopulator.populate(customerData, customer);
		customer.setRole("ROLE_USER");
		List<UserGroup> userGroups = new ArrayList<UserGroup>();
		if(CollectionUtils.isNotEmpty(customer.getUserGroups()))
			userGroups.addAll(customer.getUserGroups());
		Optional<UserGroup> userGroupOpt = rootRepository.getUserGroupByName("Prime");
		if(userGroupOpt.isPresent() && !userGroups.contains(userGroupOpt.get())) {
			userGroups.add(userGroupOpt.get());
		}
		customer.setUserGroups(userGroups);
		customerRepo.save(customer);
		if (customerData.getDefaultAddress() != null)
		{
			customerData.getDefaultAddress().setCustomer(customer.getUserName());
			A2zAddress defaultAddress = new A2zAddress();
			addressReversePopulator.populate(customerData.getDefaultAddress(), defaultAddress);
//			defaultAddress.setCustomer(customer);
			rootRepository.save(defaultAddress);
		}
		Optional<Customer> savedCustomer = customerRepo.findById(customerData.getUserName());
		CustomerData savedCustomerData = new CustomerData();
		if(savedCustomer.isPresent()) customerPopulator.populate(savedCustomer.get(), savedCustomerData);
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
	public List<AdPostData> retriveAllMyAds(String userName){
		List<AdPostData> myAdDataList = new ArrayList<AdPostData>();
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		if(customerOpt.isPresent()) {
			Customer customer = customerOpt.get();
			List<AdPost> myAdList = customer.getAdList();
			myAdList.forEach(ad->{
				AdPostData adData = new AdPostData();
				adPostPopulator.populate(ad, adData);
				myAdDataList.add(adData);
			});
		}
		return Collections.unmodifiableList(myAdDataList);
	}

	@Override
	public List<OrderData> getAllMyOrders(String userName){
		List<OrderData> orderDataList = new ArrayList<OrderData>();
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		if(customerOpt.isPresent()) {
			Customer customer = customerOpt.get();			
			List<A2zOrder> myOrdList = rootRepository.getMyOrders(customer);
			myOrdList.forEach(ord->{
				OrderData ordData = new OrderData();
				orderPopulator.populate(ord, ordData);
				orderDataList.add(ordData);
			});
		}
		return Collections.unmodifiableList(orderDataList);
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
	public List<ApprovalRequestData> getAllApprovalRequests(String userName){
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		List<ApprovalRequestData> approvalRequestDataList = new ArrayList<ApprovalRequestData>();
		if(customerOpt.isPresent()) {
			List<ApprovalRequest> approvalRequests = rootRepository.getAllApprovalRequests(customerOpt.get());
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
		return approvalRequestDataList;
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
	
}