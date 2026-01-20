package com.a2z.search.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.GeoDistanceOrder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.a2z.search.dao.AdPostSearch;




public interface SearchA2zPostRepository extends ElasticsearchRepository<AdPostSearch, Long>{

	 Optional<AdPostSearch> findById(Long id) ;
	 
	 @Query("{\"bool\": {\"must\": [{\"match\": {\"isActive\": \"?0\"}}]}}")
	 Iterable<AdPostSearch> findAllByIsActive(boolean isActive);
	 
	 @Query("{\"bool\": {\"must\": [{\"match\": {\"productName\": \"?0\"}}]}}")
	 Page<AdPostSearch> findByProductNameLike(String productName,PageRequest P);
	 
		/*
		 * @Query("{\"bool\": {\"must\": [{\"match\": {\"categories.categoryCode\": \"?0\"}}]}}"
		 * )
		 */
	 @Query("{\"bool\": {\"should\": [{\"match\": {\"categories.categoryCode\": \"?0\"}},{\"match\": {\"productName\": \"?1\"}}]}}")
	 Page<AdPostSearch> findByCategoryCodeOrProductName(String categoryCode,String productName, PageRequest pageRequest);
	 
	
		/*
		 * @Query(
		 * "{\"bool\":{\"should\":[{\"match\":{\"categories.categoryCode\":\"?0\"}},{\"match\":{\"productName\":\"?1\"}}],\"must\":[{\"range\":{\"latitude\":{\"gte\":\"?2\",\"lte\":\"?4\"}}},{\"range\":{\"longitude\":{\"gte\":\"?3\",\"lte\":\"?5\"}}}]}}")
		 */	
	 @Query("{\"bool\":{\"must\":[{\"bool\":{\"should\":[{\"match\":{\"categories.categoryCode\":\"?0\"}},{\"match\":{\"productName\":\"?1\"}}]}},{\"bool\":{\"must\":[{\"range\":{\"latitude\":{\"gte\":\"?2\",\"lte\":\"?4\"}}},{\"range\":{\"longitude\":{\"gte\":\"?3\",\"lte\":\"?5\"}}}]}}]}}")
	 Page<AdPostSearch> findByCategoryCodeOrProductNameAndLatLong(String categoryCode,String productName,double lat1, double long1, double lat2 ,double long2,PageRequest pageRequest);

	 }
