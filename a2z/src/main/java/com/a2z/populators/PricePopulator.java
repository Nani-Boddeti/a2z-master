package com.a2z.populators;

import org.springframework.core.convert.ConversionException;

import com.a2z.dao.Price;
import com.a2z.data.PriceData;

public class PricePopulator implements Populator<Price,PriceData>{

	@Override
	public void populate(Price source, PriceData target) throws ConversionException {
		target.setAmount(source.getAmount());
		target.setCurrency(source.getCurrency());
		target.setId(source.getId());
		
	}

}
