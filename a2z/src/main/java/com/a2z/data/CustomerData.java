package com.a2z.data;

import java.util.List;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Validated
public class CustomerData extends RootData {

	@Pattern(regexp = "^([a-zA-z0-9])*(?=(.*[!@#$%^&*()\\\\-__+.]))?" , message="{validation.username.regex}")
	@Size(min = 4, message = "{validation.userName.size.too_short}")
	@Size(max = 15, message = "{validation.userName.size.too_long}")
	private String userName;
	@Pattern(regexp = "^(?=(.*[a-z]){1,})(?=(.*[A-Z]){1,})(?=(.*[0-9]){1,})(?=(.*[!@#$%^&*()\\-__+.]){1,}).{5,}$" , message = "{validation.password.regex}")
	@Size(min = 5, message = "{validation.password.size.too_short}")
	@Size(max = 16, message = "{validation.password.size.too_long}")
	private String password;
	@Pattern(regexp = "^(?=(.*[a-z]){1,})(?=(.*[A-Z]){1,})(?=(.*[0-9]){1,})(?=(.*[!@#$%^&*()\\-__+.]){1,}).{5,}$" , message = "{validation.password.regex}")
	@Size(min = 5, message = "{validation.password.size.too_short}")
	@Size(max = 16, message = "{validation.password.size.too_long}")
	private String confirmPassword;
	@Email(message = "${validation.email}")
	private String email;
	@Pattern(regexp = "^[a-zA-Z0-9[ ]*]*")
	@Size(min = 2, message = "{validation.firstName.size.too_short}")
	@Size(max = 15, message = "{validation.firstName.size.too_long}")
	private String firstName;

	@Pattern(regexp = "^[a-zA-Z0-9[ ]*]*")

	@Size(min = 2, message = "{validation.lastName.size.too_short}")

	@Size(max = 15, message = "{validation.lastName.size.too_long}")

	private String lastName;

	@Pattern(regexp = "^[a-zA-Z0-9[ ]*]*" , message = "{validation.title}")

	@Size(min = 2, message = "{validation.title.size.too_short}")

	@Size(max = 10, message = "{validation.title.size.too_long}")

	private String title;

	private PaymentInfoData defaultPaymentInfo;

	private CountryData defaultCountry;
	@Pattern(regexp = "^[0-9]*" , message="{validation.phone}")
	@Size(min = 8, message = "{validation.phoneNumber.size.too_short}")
	@Size(max = 12, message = "{validation.phoneNumber.size.too_long}")
	private String phoneNumber;

	private List<String> userGroupNames;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public PaymentInfoData getDefaultPaymentInfo() {
		return defaultPaymentInfo;
	}

	public void setDefaultPaymentInfo(PaymentInfoData defaultPaymentInfo) {
		this.defaultPaymentInfo = defaultPaymentInfo;
	}

	public CountryData getDefaultCountry() {
		return defaultCountry;
	}

	public void setDefaultCountry(CountryData defaultCountry) {
		this.defaultCountry = defaultCountry;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<String> getUserGroupNames() {
		return userGroupNames;
	}

	public void setUserGroupNames(List<String> userGroupNames) {
		this.userGroupNames = userGroupNames;
	}

}
