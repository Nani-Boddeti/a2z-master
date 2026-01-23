package com.a2z.cronjob;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.a2z.search.dao.A2zCategorySearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.a2z.dao.A2zMedia;
import com.a2z.dao.AdPost;
import com.a2z.dao.Customer;
import com.a2z.enums.PrimeStatus;
import com.a2z.dao.PrimeUser;
import com.a2z.dao.UserGroup;
import com.a2z.persistence.A2zAdPostRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.search.dao.A2zMediaSearch;
import com.a2z.search.dao.AdPostSearch;
import com.a2z.search.dao.MediaContainerSearch;
import com.a2z.search.persistence.SearchA2zPostRepository;
import com.a2z.search.service.SearchUtil;

@Component
public class Scheduler {

	@Autowired
	A2zAdPostRepository adPostRepository;
	
	@Autowired
	SearchUtil searchUtil;
	
	@Autowired
	RootRepository rootRepo;
	
	@Autowired
	PODCustomerRepository customerRepo;
	
	@Autowired
	SearchA2zPostRepository searchRepo;
	
	static int DAYS_OFFSET = 30;
	
	/* Job Scheduled to trigger every 2 hours */
	 //@Scheduled(cron = "0 0 */2 ? * *")
	@Scheduled(cron="0 */2 * ? * *")
	   public void AdPostSch() {
	      // get only the posts which are to be index.. below method will get all posts.. which is not correct.
		 List<AdPost>  adsList = adPostRepository.findNotIndexedAds(false, true);
		 List<AdPostSearch> adPostSearchList = new ArrayList<AdPostSearch>();
		 adsList.forEach(ad->{
			 AdPostSearch adPostSearch = new AdPostSearch();
			 adPostSearch.setActive(ad.isActive());
				/* adPostSearch.setApprovalRequest(ad.getApprovalRequest()); */
			 List<A2zCategorySearch> categorySearchList = new ArrayList<A2zCategorySearch>();
			 ad.getCategories().forEach(a2zCategory -> {
				 A2zCategorySearch a2zCategorySearch = new A2zCategorySearch();
				 a2zCategorySearch.setCategoryCode(a2zCategory.getCategoryCode());
				 a2zCategorySearch.setCategoryName(a2zCategory.getCategoryName());
				 categorySearchList.add(a2zCategorySearch);
			 });
			 adPostSearch.setCategories(categorySearchList);
				/* adPostSearch.setCustomer(ad.getCustomer()); */
			 adPostSearch.setDescription(ad.getDescription());
			 adPostSearch.setId(ad.getId());
			 GeoPoint gps = new GeoPoint(ad.getSourceAddress().getLatitude() , ad.getSourceAddress().getLongitude());
				  adPostSearch.setLatitude(ad.getSourceAddress().getLatitude());
				  adPostSearch.setLongitude(ad.getSourceAddress().getLongitude());
			 adPostSearch.setGeoPoint(gps);
			 MediaContainerSearch mediaContainerSearch = new MediaContainerSearch();
			 mediaContainerSearch.setCode(ad.getMediaContainer().getCode());
			 List<A2zMedia> mediaList = new ArrayList<A2zMedia>();
			 mediaList.addAll(ad.getMediaContainer().getMedias());
			 List<A2zMediaSearch> mediaSearchList = new ArrayList<A2zMediaSearch>();
			 mediaList.forEach(media->{
				 A2zMediaSearch mediaSearch = new A2zMediaSearch();
				 mediaSearch.setAbsolutePath(media.getAbsolutePath());
				 mediaSearch.setFileName(media.getFileName());
				 mediaSearch.setLatitude(media.getLatitude());
				 mediaSearch.setLongitude(media.getLongitude());
				mediaSearch.setMap(media.isMap());
				mediaSearch.setMediaContainerCode(media.getMediaContainer().getCode());
				mediaSearch.setMime(media.getMime());
				mediaSearchList.add(mediaSearch);
			 });
			 mediaContainerSearch.setMedia(mediaSearchList);
			 adPostSearch.setMediaContainer(mediaContainerSearch);
			 adPostSearch.setPriceId(ad.getPrice().getId());
			 adPostSearch.setProductName(ad.getProductName());
			 adPostSearch.setCustomerUserName(ad.getCustomer().getUserName());
			 adPostSearchList.add(adPostSearch);
			 ad.setIndexed(true);
			 adPostRepository.save(ad);
			 
		 });
		 searchUtil.startIndexAll(adPostSearchList);
	   }
	
	@Scheduled(cron="0 15 1/1 ? * *")
	public void adPostUpdateStatus() {
		 List<AdPost>  adsList = adPostRepository.findByActive(true);
		 adsList.stream().forEach(ad->{
			 LocalDate currentDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			 LocalDate modifiedTime = ad.getModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(DAYS_OFFSET);
			 if(currentDate.isAfter(modifiedTime)) {
				 ad.setActive(false);
				 adPostRepository.save(ad);
			 }
		 });
	}
	
	@Scheduled(cron="0 0 5 ? * *")
	public void markPrimeUsersInactive() {
		LocalDate currentDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		 List<PrimeUser>  primeUserList = rootRepo.getActivePrimeUserAndExpired(true, currentDate);
		 primeUserList.stream().forEach(pu->{
			 pu.setIsActive(false);
			 pu.setStatus(PrimeStatus.EXPIRED);
				 rootRepo.save(pu);
			Customer customer = pu.getCustomer();
			List<UserGroup> userGroups = Optional.ofNullable(pu.getCustomer().getUserGroups()).stream().flatMap(List::stream)
			.filter(ug->!ug.getUserGroupName().equalsIgnoreCase("Prime")).collect(Collectors.toList());
			customer.setUserGroups(userGroups);
			customerRepo.save(customer);
		 });
	}
	
	@Scheduled(cron="0 */3 * ? * *")
	//@Scheduled(cron="0 5 1/1 ? * *")
	public void makeAdsInactivate() {
		Iterable<AdPostSearch> adPostSearchItr = searchRepo.findAllByIsActive(true);
		adPostSearchItr.forEach(ad ->{
			Optional<AdPost> adPost = adPostRepository.findById(ad.getId());
			if(adPost.isPresent()) {
				if(!adPost.get().isActive()) {
					ad.setActive(false);
					searchRepo.save(ad);					
				} 
			} else {
				ad.setActive(false);
				searchRepo.save(ad);
			}
		});
	
	}
}
