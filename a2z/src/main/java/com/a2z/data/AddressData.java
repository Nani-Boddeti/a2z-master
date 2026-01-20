package com.a2z.data;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Validated
public class AddressData extends RootData {

	private Long id;
	@Pattern(regexp="^[a-zA-Z0-9[ ]*]*")
	@Size(min = 2, message = "{validation.firstName.size.too_short}")
	@Size(max = 30, message = "{validation.firstName.size.too_long}")
	private String firstName;
	@Pattern(regexp="^[a-zA-Z0-9[ ]*]*")
	@Size(min = 2, message = "{validation.lastName.size.too_short}")
	@Size(max = 30, message = "{validation.lastName.size.too_long}")
	private String lastName;
	@Pattern(regexp="^[a-zA-Z0-9[ ]*]*")
	@Size(min = 2, message = "{validation.line1.size.too_short}")
	@Size(max = 30, message = "{validation.line1.size.too_long}")
	private String line1;
	@Pattern(regexp="^[a-zA-Z0-9[ ]*]*")
	@Size(min = 2, message = "{validation.line2.size.too_short}")
	@Size(max = 30, message = "{validation.line2.size.too_long}")
	private String line2;
	@Pattern(regexp="^[a-zA-Z0-9[ ]*]*")
	@Size(min = 2, message = "{validation.apartment.size.too_short}")
	@Size(max = 30, message = "{validation.apartment.size.too_long}")
	private String apartment;
	@Pattern(regexp="^[a-zA-Z0-9[ ]*]*")
	@Size(min = 2, message = "{validation.building.size.too_short}")
	@Size(max = 30, message = "{validation.building.size.too_long}")
	private String building;
	@Pattern(regexp="^[a-zA-Z0-9[ ]*]*")
	@Size(min = 8, message = "{validation.cellphone.size.too_short}")
	@Size(max = 12, message = "{validation.cellphone.size.too_long}")
	@Pattern(regexp = "/^\\d+$/")
	private String cellphone;
	@Pattern(regexp="^[a-zA-Z0-9[ ]*]*")
	@Size(min = 8, message = "{validation.company.size.too_short}")
	@Size(max = 30, message = "{validation.company.size.too_long}")
	private String company;
	@Valid
	private CountryData country;
	@Pattern(regexp="^[a-zA-Z0-9[ ]*]*")
	@Size(min = 8, message = "{validation.district.size.too_short}")
	@Size(max = 30, message = "{validation.district.size.too_long}")
	private String district;
	@Email
	private String email;
	@Pattern(regexp="^[a-zA-Z0-9[ ]*]*")
	@Size(min = 8, message = "{validation.customerId.size.too_short}")
	@Size(max = 30, message = "{validation.customerId.size.too_long}")
	private String customerId;
	private double latitude;
	private double longitude;
	private boolean isDefaultAddress;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public String getLine1() {
		return line1;
	}
	public void setLine1(String line1) {
		this.line1 = line1;
	}
	public String getLine2() {
		return line2;
	}
	public void setLine2(String line2) {
		this.line2 = line2;
	}
	public String getApartment() {
		return apartment;
	}
	public void setApartment(String apartment) {
		this.apartment = apartment;
	}
	public String getBuilding() {
		return building;
	}
	public void setBuilding(String building) {
		this.building = building;
	}
	public String getCellphone() {
		return cellphone;
	}
	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public CountryData getCountry() {
		return country;
	}
	public void setCountry(CountryData country) {
		this.country = country;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCustomer() {
		return customerId;
	}
	public void setCustomer(String customer) {
		this.customerId = customer;
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
	public boolean isDefaultAddress() {
		return isDefaultAddress;
	}

	public void setDefaultAddress(boolean isDefaultAddress) {
		this.isDefaultAddress = isDefaultAddress;
	}

}
