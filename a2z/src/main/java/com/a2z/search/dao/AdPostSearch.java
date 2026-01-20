package com.a2z.search.dao;


import java.util.List;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import com.a2z.dao.A2zCategory;
import jakarta.persistence.Id;


@Document(indexName = "adposts")
public class AdPostSearch {


	@Id
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	private String Description;
	

	
	private Long priceId;
	

	@Field(type = FieldType.Nested, includeInParent = true)
	private MediaContainerSearch mediaContainer;
	

	
	  private String customerUserName;


	private boolean isActive;

	@Field(type = FieldType.Nested, includeInParent = true)
	private List<A2zCategory> categories;


	/*
	 * @Field(type = FieldType.Nested, includeInParent = true) private
	 * ApprovalRequest approvalRequest;
	 */
	
	private String productName;
	
	private GeoPoint geoPoint;
	
	private AddressSearch addressSearch;
	
	private double latitude;
	private double longitude;
	
	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public MediaContainerSearch getMediaContainer() {
		return mediaContainer;
	}

	public void setMediaContainer(MediaContainerSearch mediaContainer) {
		this.mediaContainer = mediaContainer;
	}

	/*
	 * public Customer getCustomer() { return customer; }
	 * 
	 * public void setCustomer(Customer customer) { this.customer = customer; }
	 */

	public List<A2zCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<A2zCategory> categories) {
		this.categories = categories;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/*
	 * public ApprovalRequest getApprovalRequest() { return approvalRequest; }
	 * 
	 * public void setApprovalRequest(ApprovalRequest approvalRequest) {
	 * this.approvalRequest = approvalRequest; }
	 */

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Long getPriceId() {
		return priceId;
	}

	public void setPriceId(Long priceId) {
		this.priceId = priceId;
	}

	public String getCustomerUserName() {
		return customerUserName;
	}

	public void setCustomerUserName(String customerUserName) {
		this.customerUserName = customerUserName;
	}

	public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public AddressSearch getAddressSearch() {
		return addressSearch;
	}

	public void setAddressSearch(AddressSearch addressSearch) {
		this.addressSearch = addressSearch;
	}

	
}
