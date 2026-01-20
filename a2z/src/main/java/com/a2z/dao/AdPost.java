package com.a2z.dao;


import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.*;
@Entity
@Table(indexes = {@Index(name = "adPost_idx", columnList = "id")})

public class AdPost extends RootEntity{


	
	private String Description;
	
	@OneToOne(fetch = FetchType.EAGER ,cascade = CascadeType.REFRESH)
	@JoinColumn(name = "price_id", referencedColumnName = "id")
	private Price Price;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "mediaContainer_code", referencedColumnName = "code")
	private MediaContainer mediaContainer;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "customer_userName", foreignKey = @ForeignKey(name = "adPost_customer"))
	private Customer customer;

	private boolean active;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			  name = "adPost_category", 
			  joinColumns = @JoinColumn(name = "adPost_id"), 
			  inverseJoinColumns = @JoinColumn(name = "category_id"))
	private List<A2zCategory> categories;

	@OneToOne(mappedBy="adPost")
	private ApprovalRequest approvalRequest;
	
	private String productName;
	
	private boolean isIndexed;
	
	@OneToOne(mappedBy="adPost")
	private OrderEntry  orderEntry;
	
	@OneToOne
	@JoinColumn(name = "a2zAddress_id")
	private A2zAddress sourceAddress;
	
	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public Price getPrice() {
		return Price;
	}

	public void setPrice(Price price) {
		Price = price;
	}

	public MediaContainer getMediaContainer() {
		return mediaContainer;
	}

	public void setMediaContainer(MediaContainer mediaContainer) {
		this.mediaContainer = mediaContainer;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<A2zCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<A2zCategory> categories) {
		this.categories = categories;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean isActive) {
		this.active = isActive;
	}

	public ApprovalRequest getApprovalRequest() {
		return approvalRequest;
	}

	public void setApprovalRequest(ApprovalRequest approvalRequest) {
		this.approvalRequest = approvalRequest;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public boolean isIndexed() {
		return isIndexed;
	}

	public void setIndexed(boolean isIndexed) {
		this.isIndexed = isIndexed;
	}

	public OrderEntry getOrderEntry() {
		return orderEntry;
	}

	public void setOrderEntry(OrderEntry orderEntry) {
		this.orderEntry = orderEntry;
	}

	public A2zAddress getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(A2zAddress sourceAddress) {
		this.sourceAddress = sourceAddress;
	}



	
}
