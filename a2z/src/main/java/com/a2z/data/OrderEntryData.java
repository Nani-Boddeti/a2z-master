package com.a2z.data;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Validated
public class OrderEntryData extends RootData {
	
	private Long code;
	@NotNull
	private int qty;
	@Valid
	private PriceData basePrice;
	@Valid
	private PriceData discountedPrice;
	@Valid
	private PriceData totalPrice;
	@Valid
	private AdPostData adPost;
	@Valid
	private PriceData tax;
	@Valid
	private OrderData order;

	private boolean extendedOrder;

	public Long getCode() {
		return code;
	}
	public void setCode(Long code) {
		this.code = code;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public PriceData getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(PriceData basePrice) {
		this.basePrice = basePrice;
	}
	public PriceData getDiscountedPrice() {
		return discountedPrice;
	}
	public void setDiscountedPrice(PriceData discountedPrice) {
		this.discountedPrice = discountedPrice;
	}
	public PriceData getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(PriceData totalPrice) {
		this.totalPrice = totalPrice;
	}
	public AdPostData getAdPost() {
		return adPost;
	}
	public void setAdPost(AdPostData adPost) {
		this.adPost = adPost;
	}
	public PriceData getTax() {
		return tax;
	}
	public void setTax(PriceData tax) {
		this.tax = tax;
	}
	public OrderData getOrder() {
		return order;
	}
	public void setOrder(OrderData order) {
		this.order = order;
	}

	public boolean isExtendedOrder() {
		return extendedOrder;
	}

	public void setExtendedOrder(boolean extendedOrder) {
		this.extendedOrder = extendedOrder;
	}
}
