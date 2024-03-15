package com.a2z.persistence.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.a2z.dao.A2zOrder;
import com.a2z.dao.A2zWishlist;
import com.a2z.dao.AdPost;
import com.a2z.dao.ApprovalRequest;
import com.a2z.dao.ApprovalStatus;
import com.a2z.dao.Customer;
import com.a2z.dao.OTP;
import com.a2z.dao.UserGroup;
import com.a2z.data.AdPostData;
import com.a2z.data.ApprovalRequestData;
import com.a2z.data.ApprovalRequestPostData;
import com.a2z.data.CustomerData;
import com.a2z.data.OTPFormData;
import com.a2z.data.OrderData;
import com.a2z.data.WishlistData;
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
public class DefaultCustomerService {

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
	
	public DefaultCustomerService() {
		
	}
	public DefaultCustomerService(PODCustomerRepository customerRepo) {
		this.customerRepo = customerRepo;
	}
	
	public void saveCustomer(CustomerData customerData) {
		String encodedPassword = passwordEncoder.encode(customerData.getPassword());
		Customer customer = new Customer(customerData.getUserName(),encodedPassword,customerData.getEmail());
		customerReversePopulator.populate(customerData, customer);
		customer.setRole("ROLE_USER");
		List<UserGroup> userGroups = new ArrayList<UserGroup>();
		if(CollectionUtils.isNotEmpty(customer.getUserGroups()))
			userGroups.addAll(customer.getUserGroups());
		Optional<UserGroup> userGroupOpt = rootRepository.getUserGroupByName("Basic");
		if(userGroupOpt.isPresent() && !userGroups.contains(userGroupOpt.get())) {
			userGroups.add(userGroupOpt.get());
		}
		customer.setUserGroups(userGroups);
		customerRepo.save(customer);
	}
	
	public void deleteCustomer(Long id) {
		customerRepo.deleteById(null);
	}
	
	public Customer getUserByEmailORPhone(String phone , String email) {
		return customerRepo.getUserByEmailORPhone(phone, email);
	}
	
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
	
	public CustomerData getCustomerProfile(String userName) {
		CustomerData customerData = new CustomerData();
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		if(customerOpt.isPresent()) {
			Customer customer = customerOpt.get();
				customerPopulator.populate(customer, customerData);
		}
		return customerData;
	}
	
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
	
	public ApprovalRequestData submitApprovalRequest(ApprovalRequestPostData requestData ,String userName){
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		ApprovalRequestData approvalReqData = new ApprovalRequestData();
		if(customerOpt.isPresent()) {
			Optional<ApprovalRequest> approvalRequestOpt = rootRepository.getApprovalRequestDetails(requestData.getId(),customerOpt.get());
			if(approvalRequestOpt.isPresent()) {
				ApprovalRequest approvalRequest = approvalRequestOpt.get();
				approvalRequest.setApprovalStatus(ApprovalStatus.valueOf(requestData.getStatus()));
				rootRepository.save(approvalRequest);
					approvalReqData.setId(approvalRequest.getId());
					approvalReqData.setStatus(approvalRequest.getApprovalStatus().toString());
					AdPostData adPostData = new AdPostData();
					adPostPopulator.populate(approvalRequest.getAdPost(), adPostData);
			}	
		}
		
		return approvalReqData;
	}
	
	public void addToWishlist(Long id,String userName) {
		Optional<AdPost> adOpt = adPostRepository.findById(id);
		Optional<Customer> customerOpt = customerRepo.findById(userName); 
		if(adOpt.isPresent() && customerOpt.isPresent()) {
			AdPost ad = adOpt.get();
			Customer customer = customerOpt.get();
			A2zWishlist wishlist = new A2zWishlist();
			wishlist.setCustomer(customer);
			List<AdPost> newAdList = new ArrayList<>();
			newAdList.addAll(wishlist.getAds());
			newAdList.add(ad);
			wishlist.setAds(newAdList);
			rootRepository.save(wishlist);
		}
	}
	
	public WishlistData getWishlist(Long id,String userName) {
		Optional<Customer> customerOpt = customerRepo.findById(userName); 
		WishlistData wishlistData = new WishlistData();
		if(customerOpt.isPresent()) {
			Optional<A2zWishlist> wishlistOpt = rootRepository.getWishlistDetails(id,customerOpt.get());
			if(wishlistOpt.isPresent() && customerOpt.isPresent()) {
				A2zWishlist wishList = wishlistOpt.get();
				wishlistPopulator.populate(wishList, wishlistData);
			}
		}
		return wishlistData;
		
		
	}
	
	public List<WishlistData> getAllWishlist(String userName) {
		Optional<Customer> customerOpt = customerRepo.findById(userName);
		List<WishlistData> listWishlistData = new ArrayList<WishlistData>();
		if(customerOpt.isPresent()) {
			List<A2zWishlist> wishlistList = rootRepository.getWishlistForCustomer(customerOpt.get());
			wishlistList.stream().forEach(wishlist->{
				WishlistData wishlistData = new WishlistData();
				wishlistPopulator.populate(wishlist, wishlistData);
				listWishlistData.add(wishlistData);
			});
		}
		return listWishlistData;
	}
	
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