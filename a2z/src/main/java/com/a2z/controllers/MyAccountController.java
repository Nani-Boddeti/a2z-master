package com.a2z.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.a2z.services.interfaces.CustomerService;
import com.a2z.services.interfaces.MediaService;
import com.a2z.services.interfaces.WishlistService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.a2z.data.AdPostData;
import com.a2z.data.ApprovalRequestData;
import com.a2z.data.ApprovalRequestPostData;
import com.a2z.data.CustomerData;
import com.a2z.data.MediaContainerData;
import com.a2z.data.OrderData;
import com.a2z.data.WishlistData;
import com.a2z.services.impl.DefaultCustomerService;
import com.a2z.services.impl.DefaultMediaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@ResponseBody
@RequestMapping("/myAccount")
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
	public CustomerData getProfile(HttpServletRequest request) {
		String userName = getSessionUserName();
		if(StringUtils.isEmpty(userName)) {
			return null;
		}
		return customerService.getCustomerProfile(userName);
	}
	
	@GetMapping("/myAds")
	public List<AdPostData> viewAllMyAds( HttpServletRequest request){
		String userName = getSessionUserName();
		List<AdPostData> adsList = new ArrayList<AdPostData>();
		if(StringUtils.isNotEmpty(userName)) {
			adsList = customerService.retriveAllMyAds(userName);
		}
		return Collections.unmodifiableList(adsList);
	}
	
	@GetMapping("/myAdOrders")
	public List<OrderData> viewAllMyAdOrders( HttpServletRequest request){
		String userName = getSessionUserName();
		List<OrderData> ordList = new ArrayList<OrderData>();
		if(StringUtils.isNotEmpty(userName)) {
			ordList = customerService.getAllMyOrders(userName);
		}
		return Collections.unmodifiableList(ordList);
	}
	
	@GetMapping("/order/{orderId}")
	public OrderData viewOrder(@PathVariable @Valid final Long orderId, HttpServletRequest request){
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
	public List<ApprovalRequestData> getApprovalRequests(HttpServletRequest request){
		String userName = getSessionUserName();
		return customerService.getAllApprovalRequests(userName);
	}
	
	@PostMapping("/approvalRequests/update")
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
}
