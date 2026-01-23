package com.a2z.dao;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class Customer {

	public Customer(String username, String password, String email) {
		this.userName = username;
		this.password = password;
		/* this.authorities = (Set<GrantedAuthority>) authorities; */
		this.isDisabled = false;
		this.lastLogin = new Date();
		this.email = email;
		this.createdDate = new Date();
	}

	public Customer() {
		this.isDisabled = false;
		this.lastLogin = new Date();
		this.createdDate = new Date();
		this.userName = "";
		/* this.authorities = null; */
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 5242540514408950189L;

	@JsonIgnore // Ensure password is not serialized
	private String password;

	@Id
	@Column(unique = true, nullable = false)
	private final String userName;

	/* private final Set<GrantedAuthority> authorities; */
	private String firstName;
	private String lastName;
	private String title;
	private Date createdDate;
	private boolean isDisabled;
	private Date lastLogin;
	private Date deactivationDate;
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "paymentInfo_id", referencedColumnName = "id")
	private PaymentInfo defaultPaymentInfo;
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
	@JoinColumn(name = "defaultCountry_isoCode" , referencedColumnName="isoCode")
	private Country defaultCountry;
	private String email;
	@Column(unique = true, nullable = false)
	private String phoneNumber;
	@OneToMany(mappedBy="customer", orphanRemoval=true)
	private List<A2zAddress> address;
	@OneToMany(mappedBy="customer", orphanRemoval=true)
	private List<A2zOrder> orders;
	@OneToMany(mappedBy="customer", orphanRemoval=true)
	private List<AdPost> adList;
	@ManyToMany
	@JoinTable(
			  name = "usergroup_customer", 
			  joinColumns = @JoinColumn(name = "customer_id"), 
			  inverseJoinColumns = @JoinColumn(name = "userGroup_id"))
	private List<UserGroup> userGroups;
	
	@OneToMany(mappedBy="customer", orphanRemoval=true)
	private List<ApprovalRequest> approvalRequest;
	
	@OneToMany(mappedBy="customer", orphanRemoval=true)
	private List<A2zWishlist> a2zWishlist;
	
	private String role;
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Date isLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getDeactivationDate() {
		return deactivationDate;
	}

	public void setDeactivationDate(Date deactivationDate) {
		this.deactivationDate = deactivationDate;
	}

	public PaymentInfo getDefaultPaymentInfo() {
		return defaultPaymentInfo;
	}

	public void setDefaultPaymentInfo(PaymentInfo defaultPaymentInfo) {
		this.defaultPaymentInfo = defaultPaymentInfo;
	}

	public Country getDefaultCountry() {
		return defaultCountry;
	}

	public void setDefaultCountry(Country defaultCountry) {
		this.defaultCountry = defaultCountry;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	/*
	 * public Set<GrantedAuthority> getAuthorities() { return authorities; }
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public List<A2zAddress> getAddress() {
		return address;
	}

	public void setAddress(List<A2zAddress> address) {
		this.address = address;
	}

	public List<A2zOrder> getOrders() {
		return orders;
	}

	public void setOrders(List<A2zOrder> orders) {
		this.orders = orders;
	}

	public List<AdPost> getAdList() {
		return adList;
	}

	public void setAdList(List<AdPost> adList) {
		this.adList = adList;
	}

	public List<UserGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	

	public List<A2zWishlist> getA2zWishlist() {
		return a2zWishlist;
	}

	public void setA2zWishlist(List<A2zWishlist> a2zWishlist) {
		this.a2zWishlist = a2zWishlist;
	}

	public List<ApprovalRequest> getApprovalRequest() {
		return approvalRequest;
	}

	public void setApprovalRequest(List<ApprovalRequest> approvalRequest) {
		this.approvalRequest = approvalRequest;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
