package com.a2z.populators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.OrderEntry;
import com.a2z.data.AdPostData;
import com.a2z.data.OrderEntryData;
import com.a2z.data.PriceData;

public class OrderEntryPopulator implements Populator<OrderEntry,OrderEntryData> {
	
	@Autowired
	AdPostPopulator adPostPopulator;
	
	@Autowired
	PricePopulator pricePopulator;
	
	@Override
	public void populate(OrderEntry source, OrderEntryData target) throws ConversionException {
		AdPostData adData = new AdPostData();
		adPostPopulator.populate(source.getAdPost(), adData);
		target.setAdPost(adData);
		PriceData basePrice = new PriceData();
		pricePopulator.populate(source.getBasePrice(), basePrice);
		target.setBasePrice(basePrice);
		target.setCode(source.getCode());
		PriceData discountedPrice = new PriceData();
		pricePopulator.populate(source.getDiscountedPrice(), discountedPrice);
		target.setDiscountedPrice(discountedPrice);
		target.setQty(source.getQty());		
		target.setTax(null);
		PriceData totalPrice = new PriceData();
		pricePopulator.populate(source.getTotalPrice(), totalPrice);
		target.setTotalPrice(totalPrice);
		
	}

}
