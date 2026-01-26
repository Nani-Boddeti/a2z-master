package com.a2z.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.a2z.data.*;
import com.a2z.services.interfaces.CustomerService;
import com.a2z.services.interfaces.MediaService;
import com.a2z.services.interfaces.WishlistService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.a2z.services.impl.ForgotPasswordTokenGeneratorService.ResetToken;
import com.a2z.services.impl.DefaultCustomerService;
import com.a2z.services.impl.DefaultMediaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@ResponseBody
@RequestMapping("/api/myAccount")
@Secured("SCOPE_app.read")
@Validated
public class MyAccountController extends RootController {
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	MediaService mediaService;

	@Autowired
	WishlistService wishlistService;

	@GetMapping("/profile")
	@ResponseBody
	public CustomerData getProfile(HttpServletRequest request) {
		String userName = getSessionUserName();
		if(StringUtils.isEmpty(userName)) {
			return null;
		}
		return customerService.getCustomerProfile(userName);
	}

	@PutMapping("/profile/update")
	public CustomerData getProfileUpdate(HttpServletRequest request, @RequestBody @Valid CustomerProfileUpdateData customerProfileUpdateData) {
		String userName = getSessionUserName();
		if(StringUtils.isNotEmpty(userName) && userName.equals(customerProfileUpdateData.getUserName())) {
			return customerService.updateCustomer(customerProfileUpdateData);
		}
		CustomerData customerData = new CustomerData();
		customerData.setErrorMessage("Unauthorized profile update attempt");
		return customerData;
	}
	@GetMapping("/myAds")
	@ResponseBody
	public PagedAdPostResult viewAllMyAds( HttpServletRequest request,@RequestParam(required = false) Integer pageNumber
			, @RequestParam(required = false) Integer pageSize){
		String userName = getSessionUserName();
		if(StringUtils.isNotEmpty(userName)) {
			return customerService.retriveAllMyAds(userName, pageNumber, pageSize);
		}
		return new PagedAdPostResult();
	}
	
	@GetMapping("/myAdOrders")
	@ResponseBody
	public PagedA2zOrderResult viewAllMyAdOrders(HttpServletRequest request,
												 @RequestParam(required = false) Integer pageNumber
			, @RequestParam(required = false) Integer pageSize){
		String userName = getSessionUserName();
		if(StringUtils.isNotEmpty(userName)) {
			return customerService.getAllMyOrders(userName, pageNumber, pageSize);
		}
		return new PagedA2zOrderResult();
	}
	@GetMapping("/myAdOrders/status")
	@ResponseBody
	public PagedA2zOrderResult viewAllMyAdOrdersByStatus(HttpServletRequest request,@RequestParam(required = true) String status,
												 @RequestParam(required = false) Integer pageNumber
			, @RequestParam(required = false) Integer pageSize){
		String userName = getSessionUserName();
		if(StringUtils.isNotEmpty(userName)) {
			return customerService.getAllMyOrdersInStatus(userName, pageNumber, pageSize,status);
		}
		return new PagedA2zOrderResult();
	}

	@GetMapping("/order/details/")
	@ResponseBody
	public OrderData viewOrder(@RequestParam(required = true) @Valid final Long orderId, HttpServletRequest request){
		String userName = getSessionUserName();
		OrderData orderData = new OrderData();
		if(StringUtils.isNotEmpty(userName)) {
			orderData = customerService.viewOrder(orderId,userName);
		}
		return orderData;
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request){
		request.getSession().invalidate();	
		return "redirect://";
	}
	
	@GetMapping("/approvalRequests")
	@ResponseBody
	public PagedA2zApprovalResult getApprovalRequests(HttpServletRequest request,@RequestParam(required = false) Integer pageNumber
			, @RequestParam(required = false) Integer pageSize){
		String userName = getSessionUserName();
		return customerService.getAllApprovalRequests(userName, pageNumber, pageSize);
	}
	
	@PostMapping("/approvalRequests/update")
	@ResponseBody
	public ApprovalRequestData submitApprovalRequest(@RequestBody @Valid ApprovalRequestPostData requestData , HttpServletRequest request){
		String userName = getSessionUserName();
		return customerService.submitApprovalRequest(requestData,userName);
	}
	
	@PostMapping("/approvalRequests/{id}")
	public ApprovalRequestData getApprovalRequest(@PathVariable @Valid Long id , HttpServletRequest request){
		String userName = getSessionUserName();
		return customerService.getApprovalRequest(id,userName);
	}
	
	
	@PostMapping("/addToWishlist/{id}")
	public String AddToWishList(@PathVariable @Valid Long id,HttpServletRequest request){
		String userName = getSessionUserName();
		wishlistService.addToWishlist(id, userName);
		return HttpStatus.ACCEPTED.toString();
	}
	
	@GetMapping("/wishlit/{id}")
	public WishlistData getWishlist(@PathVariable @Valid Long id,HttpServletRequest request) {
		String userName = (String) request.getSession().getAttribute("currentUser");
		return wishlistService.getWishlist(id, userName);
		
	}
	
	@GetMapping("/wishlit")
	public List<WishlistData> getAllWishlist(HttpServletRequest request) {
		String userName = getSessionUserName();
		return wishlistService.getAllWishlist(userName);
		
	}
	
	@GetMapping("/proof")
	public MediaContainerData getProofMedia(HttpServletRequest request) {
		String userName = getSessionUserName();
		return mediaService.getProofMedia(userName);
	}

	@GetMapping("/deleteAccount")
	public void deleteAccount(HttpServletRequest request,@RequestParam(required = true) String userName ) {
		String sessionUserName = getSessionUserName();
		if(sessionUserName.equals(userName))
		customerService.deleteCustomer(userName);
	}


}
