package com.a2z.populators.reverse;

import java.util.Optional;

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
		target.setBasePrice(priceRepo.findById(source.getBasePrice().getId()).get());		
		target.setDiscountedPrice(priceRepo.findById(source.getDiscountedPrice().getId()).get());
		target.setQty(source.getQty());		
		target.setTax(null);
		target.setTotalPrice(priceRepo.findById(source.getTotalPrice().getId()).get());
		
	}

}
