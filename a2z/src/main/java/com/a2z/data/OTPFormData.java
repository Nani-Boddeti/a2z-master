package com.a2z.data;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Pattern;

@Validated
public class OTPFormData extends RootData {
	@Pattern(regexp="^[0-9]*")
	private String phone;
	@Pattern(regexp="^[0-9]*")
	private String OTP;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getOTP() {
		return OTP;
	}
	public void setOTP(String oTP) {
		OTP = oTP;
	}
}
