package com.a2z.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

@Validated
public class PriceData extends RootData {

	private Long id;
	@Pattern(regexp="^[a-zA-Z[ ]*]*")
	@Size(min = 2, message = "{validation.currency.size.too_short}")
	@Size(max = 10, message = "{validation.currency.size.too_long}")
	private String currency;
	@Pattern(regexp="^[0-9]*([?=.])[0-9]*")
	@Size(min = 2, message = "{validation.amount.size.too_short}")
	@Size(max = 10, message = "{validation.amount.size.too_long}")
	private String amount;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
}
