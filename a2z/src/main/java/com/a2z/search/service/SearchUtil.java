package com.a2z.search.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.GeoDistanceOrder;
import org.springframework.stereotype.Service;

import com.a2z.dao.AdPost;
import com.a2z.dao.Customer;
import com.a2z.dao.MediaContainer;
import com.a2z.dao.Price;
import com.a2z.data.AdPostData;
import com.a2z.data.AddressData;
import com.a2z.data.CustomerData;
import com.a2z.data.GPS;
import com.a2z.data.MediaContainerData;
import com.a2z.data.PriceData;
import com.a2z.persistence.A2zMediaContainerRepository;
import com.a2z.persistence.A2zPriceRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.populators.CustomerPopulator;
import com.a2z.populators.MediaContainerPopulator;
import com.a2z.populators.PricePopulator;
import com.a2z.search.dao.AdPostSearch;
import com.a2z.search.persistence.SearchA2zPostRepository;
import com.a2z.services.GeometryUtils;

@Service
public class SearchUtil {
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
	
	
	public void findByName(String prodName) {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<AdPostSearch> adPostByName
		  = searchRepo.findByProductNameLike(prodName, pageRequest);
	}
	
	public Page<AdPostSearch> findByCategoryCodeOrProductName(String category) {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<AdPostSearch> adPostByPage = searchRepo.findByCategoryCodeOrProductName(category, category, pageRequest);
		return adPostByPage;
	}
	
	public void startIndexAll(List<AdPostSearch> adPosts) {
		searchRepo.saveAll(adPosts);
	}
	
	public List<AdPostData> getAllPosts() {
		Iterable<AdPostSearch> adPostSearch = searchRepo.findAllByIsActive(true);
				List<AdPostData> adPostDataList = new ArrayList<AdPostData>();
		adPostSearch.forEach(ad->{
			AdPostData adPostData = new AdPostData();
			adPostData.setActive(true);
			adPostData.setDescription(ad.getDescription());
			adPostData.setId(ad.getId());
			adPostData.setProductName(ad.getProductName());
			CustomerData customerData = new CustomerData();
			Optional<Customer> customerOpt = customerRepo.findById(ad.getCustomerUserName());
			if(customerOpt.isPresent())
			customerPopulator.populate(customerOpt.get(), customerData);
			adPostData.setCustomer(customerData);
			MediaContainerData mediaContainerData = new MediaContainerData();
			Optional<MediaContainer> mediaContainerOpt = mediaContainerRepo.findById(ad.getMediaContainer().getCode());
			if(mediaContainerOpt.isPresent())
				mediaContainerPopulator.populate(mediaContainerOpt.get(), mediaContainerData);
			adPostData.setMediaContainerData(mediaContainerData);
			PriceData priceData = new PriceData();
			Optional<Price> priceOpt = priceRepo.findById(ad.getPriceId());
			if(priceOpt.isPresent()) 
				pricePopulator.populate(priceOpt.get(), priceData);
			adPostData.setPrice(priceData);
			AddressData sourceAddressData = new AddressData();
			sourceAddressData.setLatitude(ad.getLatitude());
			sourceAddressData.setLongitude(ad.getLongitude());
			adPostData.setSourceAddress(sourceAddressData);
			adPostDataList.add(adPostData);
		});
		
		return adPostDataList;
 	}
	
	public void findByNameAndCatAndLatLong(String prodName , double lat , double lon , double rad) {
		//Sort sort = Sort.by(new GeoDistanceOrder("geoPoint", new GeoPoint(lat, lon)));
		PageRequest pageRequest = PageRequest.of(0, 10);
		GPS gps = new GPS();
		gps.setDecimalLatitude(lat);
		gps.setDecimalLongitude(lon);
		Map<String, Double> map = geoUtils.getSquareOfTolerance(gps, rad);
		Page<AdPostSearch> adPostByName
		  = searchRepo.findByCategoryCodeOrProductNameAndLatLong(prodName, prodName ,map.get("lat1"),map.get("lon1"),map.get("lat2") , map.get("lon2") ,pageRequest);
	}
}
