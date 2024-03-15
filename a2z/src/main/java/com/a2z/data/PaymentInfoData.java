package com.a2z.data;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*@Validated*/
public class PaymentInfoData extends RootData {
	@Pattern(regexp="^[a-zA-Z[ ]*]*")
	@Size(min = 2, message = "{validation.paymentType.size.too_short}")
	@Size(max = 10, message = "{validation.paymentType.size.too_long}")
	private String paymentType;
	@Pattern(regexp="^[a-zA-Z[ ]*]*")
	@Size(min = 2, message = "{validation.paymentCode.size.too_short}")
	@Size(max = 10, message = "{validation.paymentCode.size.too_long}")
	private String paymentCode;
	
	private AddressData paymentAddress;
	
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getPaymentCode() {
		return paymentCode;
	}
	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}
	public AddressData getPaymentAddress() {
		return paymentAddress;
	}
	public void setPaymentAddress(AddressData paymentAddress) {
		this.paymentAddress = paymentAddress;
	}
}
