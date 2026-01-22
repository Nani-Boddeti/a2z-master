package com.a2z.services.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.a2z.dao.*;
import com.a2z.events.AdPostSubmissionEvent;
import com.a2z.services.interfaces.AdPostService;
import com.a2z.services.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.stereotype.Service;

import com.a2z.data.AdPostData;
import com.a2z.persistence.A2zAdPostRepository;
import com.a2z.persistence.A2zPriceRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.populators.AdPostPopulator;
import com.a2z.populators.reverse.AdPostReversePopulator;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;

@Service
public class DefaultAdPostService implements AdPostService {

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
	
	@Autowired
	A2zPriceRepository a2zPriceRepository;

	@Autowired
	EmailService emailService;

	@Autowired
	ApplicationEventPublisher eventPublisher;
	
	@Transactional
	@Override
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
			sendAdPostSubmissionEmail(adPost);
			adPostPopulator.populate(adPost, adPostData);
		}
		} else {
			adPostData.setErrorMessage("Customer is not eligible to post ad");
		}
		
		return adPostData;
	} 

	@Override
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

	@Override
	public AdPostData getAdById(Long id) {
			Optional<AdPost> ad = adPostRepository.findById(id);
			AdPostData adPostData = new AdPostData();
			if(ad.isPresent()) {adPostPopulator.populate(ad.get(), adPostData);} 
		return adPostData;
	} 

	@Override
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
				Price price = new Price();
				price.setAmount(adPost.getPrice().getAmount());
				price.setCurrency(adPost.getPrice().getCurrency());
				a2zPriceRepository.save(price);
				adPost.setPrice(price);
				adPostRepository.save(adPost);
				adPostPopulator.populate(adPost, adPostData);	
			} else adPostData.setErrorMessage("Corresponding Order is not Completed Yet.");
		}
		return adPostData;
	}

	@Override
	public AdPostData receivedStatusUpdate(Long id, boolean isReceived, boolean isAcivate) {
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
	
	private void sendAdPostSubmissionEmail(AdPost adPost) {
		AdPostSubmissionEvent event = new AdPostSubmissionEvent(adPost);
		eventPublisher.publishEvent(event);
	}
}
