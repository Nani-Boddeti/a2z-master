package com.a2z.populators.reverse;

import java.util.Optional;

import com.a2z.dao.Price;
import com.a2z.data.PriceData;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.AdPost;
import com.a2z.dao.OrderEntry;
import com.a2z.data.OrderEntryData;
import com.a2z.persistence.A2zAdPostRepository;
import com.a2z.persistence.A2zPriceRepository;
import com.a2z.populators.Populator;

public class OrderEntryReversePopulator implements Populator<OrderEntryData,OrderEntry>{

	@Autowired
	A2zPriceRepository priceRepo;
	
	@Override
	public void populate(OrderEntryData source, OrderEntry target) throws ConversionException {
		Price basePrice = priceRepo.findById(source.getBasePrice().getId()).get();
		Price discountedPrice = priceRepo.findById(source.getDiscountedPrice().getId()).get();
		Price totalPrice = priceRepo.findById(source.getTotalPrice().getId()).get();
		if(source.isExtendedOrder()){
			basePrice = createPriceRow(source.getBasePrice());
			if(StringUtils.equals(source.getBasePrice().getAmount(),source.getDiscountedPrice().getAmount()) ){
				discountedPrice = basePrice;
			}
			else {
				discountedPrice = createPriceRow(source.getDiscountedPrice());
			}
			if(StringUtils.equals(source.getBasePrice().getAmount(),source.getTotalPrice().getAmount()) ){
				totalPrice = basePrice;
			}else {
				totalPrice = createPriceRow(source.getTotalPrice());
				target.setTotalPrice(totalPrice);
			}
		}
		target.setBasePrice(basePrice);
		target.setDiscountedPrice(discountedPrice);
		target.setQty(source.getQty());		
		target.setTax(null);
		target.setTotalPrice(totalPrice);
		
	}

	private Price createPriceRow(PriceData priceData) {
		Price price = new Price();
		price.setAmount(priceData.getAmount());
		price.setCurrency(priceData.getCurrency());
		priceRepo.save(price);
		return price;
	}
}
