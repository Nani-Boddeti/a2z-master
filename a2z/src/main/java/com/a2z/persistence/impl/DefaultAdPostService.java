package com.a2z.persistence.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.session.Session;

import com.a2z.dao.A2zOrder;
import com.a2z.dao.AdPost;
import com.a2z.dao.Customer;
import com.a2z.dao.OrderStatus;
import com.a2z.data.AdPostData;
import com.a2z.persistence.A2zAdPostRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.populators.AdPostPopulator;
import com.a2z.populators.reverse.AdPostReversePopulator;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;

@Service
public class DefaultAdPostService {

	@Autowired
	private A2zAdPostRepository adPostRepository;
	
	@Autowired
	private AdPostPopulator  adPostPopulator;
	
	@Autowired
	private AdPostReversePopulator  adPostReversePopulator;
	
	@Autowired
	RedisSessionRepository redisSessionRepository;
	
	@Autowired
	RootRepository rootRepo;
	
	@Autowired
	DefaultCustomerService customerService;
	
	@Transactional
	public AdPostData submitAdPost(AdPostData adPostData, String userName) {
		if(customerService.isCustomerEligibleToPost(userName)) {
		if(StringUtils.isNotEmpty(userName)) {
			if(StringUtils.isBlank(adPostData.getCustomer().getUserName())) {
				adPostData.getCustomer().setUserName(userName);
			}
			AdPost adPost =new AdPost();
			adPostReversePopulator.populate(adPostData, adPost);
			adPost.setIndexed(false);
			adPost.setActive(true);
			adPost.setModifiedTime(new Date());
			adPostRepository.save(adPost);
			adPostPopulator.populate(adPost, adPostData);
		}
		} else {
			adPostData.setErrorMessage("Customer is not eligible to post ad");
		}
		
		return adPostData;
	} 
	
	public List<AdPostData> retriveAllAds() {		
		Iterable<AdPost> adsList = adPostRepository.findAll();
		List<AdPostData> targetAdsList = new ArrayList<AdPostData>();
		adsList.forEach(ad -> {
			AdPostData adPostData = new AdPostData();
			adPostPopulator.populate(ad, adPostData);
			targetAdsList.add(adPostData);
		});
		return targetAdsList;
	} 
	
	public AdPostData getAdById(Long id) {		
			Optional<AdPost> ad = adPostRepository.findById(id);
			AdPostData adPostData = new AdPostData();
			adPostPopulator.populate(ad.get(), adPostData);
		return adPostData;
	} 
	
	public AdPostData activatePost(Long id) {
		Optional<AdPost> adOpt = adPostRepository.findById(id);
		AdPostData adPostData = new AdPostData();
		if(adOpt.isPresent()) {
			AdPost adPost = adOpt.get();
			A2zOrder order = adPost.getOrderEntry().getOrder();
			if(order.getStatus().compareTo(OrderStatus.COMPLETED)== 0) {
				adPost.setActive(true);
				adPost.setIndexed(false);
				adPost.setModifiedTime(new Date());
				adPostRepository.save(adPost);
				adPostPopulator.populate(adPost, adPostData);	
			} else adPostData.setErrorMessage("Corresponding Order is not Completed Yet.");
		}
		return adPostData;
	}
	
	public AdPostData receivedStatusUpdate(Long id , boolean isReceived , boolean isAcivate) {
		Optional<AdPost> adOpt = adPostRepository.findById(id);
		AdPostData adPostData = new AdPostData();
		if(adOpt.isPresent()) {
			AdPost adPost = adOpt.get();
			if(isAcivate) {
				adPost.setActive(true);
				adPost.setIndexed(false);
				adPost.setModifiedTime(new Date());
				adPostRepository.save(adPost);
			}
			if(isReceived) {
				A2zOrder order = adPost.getOrderEntry().getOrder();
				order.setStatus(OrderStatus.COMPLETED);
				rootRepo.save(order);
			}
			adPostPopulator.populate(adPost, adPostData);	
		}

		return adPostData;
	}
	
	
}
