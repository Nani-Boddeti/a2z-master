package com.a2z.search.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.a2z.data.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import com.a2z.dao.AdPost;
import com.a2z.dao.Customer;
import com.a2z.dao.MediaContainer;
import com.a2z.dao.Price;
import com.a2z.persistence.A2zMediaContainerRepository;
import com.a2z.persistence.A2zPriceRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.populators.CustomerPopulator;
import com.a2z.populators.MediaContainerPopulator;
import com.a2z.populators.PricePopulator;
import com.a2z.search.dao.AdPostSearch;
import com.a2z.search.persistence.SearchA2zPostRepository;
import com.a2z.services.GeometryUtils;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
public class SearchUtil {
	private static final int PAGE_SIZE = 10;
	private static final int DEFAULT_PAGE_NO = 0;

	@Autowired
	ElasticsearchOperations elasticsearchOperations;

	@Autowired
	SearchA2zPostRepository searchRepo;

	@Autowired
	PODCustomerRepository customerRepo;

	@Autowired
	A2zPriceRepository priceRepo;

	@Autowired
	A2zMediaContainerRepository mediaContainerRepo;

	@Autowired
	CustomerPopulator customerPopulator;

	@Autowired
	PricePopulator pricePopulator;

	@Autowired
	MediaContainerPopulator mediaContainerPopulator;

	@Autowired
	GeometryUtils geoUtils;


	public void startIndex(AdPostSearch adPost) {
		/* elasticsearchOperations.indexOps(AdPostSearch.class).create(); */
		searchRepo.save(adPost);
	}


	public PagedAdPostResult findByName(String prodName, Integer pageNo, Integer pageSize) {
		Page<AdPostSearch> adPostByName
				= searchRepo.findByProductNameLike(prodName, getPageRequest(pageNo, pageSize));
		return populatePagedAds(adPostByName);
	}

	public PagedAdPostResult findByCategoryCodeOrProductName(String category, Integer pageNo, Integer pageSize) {
		Page<AdPostSearch> adPostByPage = searchRepo.findByCategoryCodeOrProductName(category, category, getPageRequest(pageNo, pageSize));
		return populatePagedAds(adPostByPage);
	}

	public void startIndexAll(List<AdPostSearch> adPosts) {
		searchRepo.saveAll(adPosts);
	}

	public PagedAdPostResult getAllPosts(Integer pageNo, Integer pageSize) {
		Page<AdPostSearch> adPostSearch = searchRepo.findAllByIsActivePaged(true, getPageRequest(pageNo, pageSize));
		return populatePagedAds(adPostSearch);
	}

	public PagedAdPostResult findByNameAndCatAndLatLong(String prodName, Double lat, Double lon, Double rad, Integer pageNo, Integer pageSize) {
		//Sort sort = Sort.by(new GeoDistanceOrder("geoPoint", new GeoPoint(lat, lon)));
		if (ObjectUtils.isEmpty(lat) || ObjectUtils.isEmpty(lon) || ObjectUtils.isEmpty(rad)) {
			return this.findByCategoryCodeOrProductName(prodName, pageNo, pageSize);
		}
		GPS gps = new GPS();
		gps.setDecimalLatitude(lat);
		gps.setDecimalLongitude(lon);
		Map<String, Double> map = geoUtils.getSquareOfTolerance(gps, rad);
		Page<AdPostSearch> adPostByName
				= searchRepo.findByCategoryCodeOrProductNameAndLatLong(prodName, prodName,
				map.get("lat1"), map.get("lon1"), map.get("lat2"), map.get("lon2"), getPageRequest(pageNo, pageSize));
		return populatePagedAds(adPostByName);
	}

	public PagedAdPostResult findByCategoryCode(String prodName, Integer pageNo, Integer pageSize) {
		//Sort sort = Sort.by(new GeoDistanceOrder("geoPoint", new GeoPoint(lat, lon)));
		Page<AdPostSearch> adPostByName
				= searchRepo.findByCategoryCode(prodName, getPageRequest(pageNo, pageSize));
		return populatePagedAds(adPostByName);
	}

	private PagedAdPostResult populatePagedAds(Page<AdPostSearch> adPostSearch) {
		PagedAdPostResult pagedAdPostResult = new PagedAdPostResult();
		pagedAdPostResult.setCurrentPage(adPostSearch.getPageable().getPageNumber());
		pagedAdPostResult.setTotalPages(adPostSearch.getTotalPages());

		List<AdPostData> adPostDataList = new ArrayList<AdPostData>();
		adPostSearch.getContent().forEach(ad -> {
			AdPostData adPostData = new AdPostData();
			adPostData.setActive(true);
			adPostData.setDescription(ad.getDescription());
			adPostData.setId(ad.getId());
			adPostData.setProductName(ad.getProductName());
			CustomerData customerData = new CustomerData();
			Optional<Customer> customerOpt = customerRepo.findById(ad.getCustomerUserName());
			if (customerOpt.isPresent())
				customerPopulator.populate(customerOpt.get(), customerData);
			adPostData.setCustomer(customerData);
			MediaContainerData mediaContainerData = new MediaContainerData();
			Optional<MediaContainer> mediaContainerOpt = mediaContainerRepo.findById(ad.getMediaContainer().getCode());
			if (mediaContainerOpt.isPresent())
				mediaContainerPopulator.populate(mediaContainerOpt.get(), mediaContainerData);
			adPostData.setMediaContainerData(mediaContainerData);
			PriceData priceData = new PriceData();
			Optional<Price> priceOpt = priceRepo.findById(ad.getPriceId());
			if (priceOpt.isPresent())
				pricePopulator.populate(priceOpt.get(), priceData);
			adPostData.setPrice(priceData);
			AddressData sourceAddressData = new AddressData();
			sourceAddressData.setLatitude(ad.getLatitude());
			sourceAddressData.setLongitude(ad.getLongitude());
			adPostData.setSourceAddress(sourceAddressData);
			adPostDataList.add(adPostData);
		});
		pagedAdPostResult.setAdPosts(adPostDataList);
		return pagedAdPostResult;
	}

	private PageRequest getPageRequest(Integer pageNo, Integer pageSize) {
		int pageNumber = DEFAULT_PAGE_NO;
		int size = PAGE_SIZE;
		Sort sort = Sort.by("modifiedTime").descending();
		if (ObjectUtils.isNotEmpty(pageNo) && pageNo.intValue() > 0) {
			pageNumber = pageNo.intValue() - 1;
		}
		if (ObjectUtils.isNotEmpty(pageSize)) {
			size = pageSize.intValue();
		}
		return PageRequest.of(pageNumber, size, sort);

	}
}