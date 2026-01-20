package com.a2z.populators.reverse;

import org.springframework.core.convert.ConversionException;

import com.a2z.dao.Country;
import com.a2z.data.CountryData;
import com.a2z.populators.Populator;

public class CountryReversePopulator implements Populator<CountryData, Country>{

	@Override
	public void populate(CountryData source, Country target) throws ConversionException {
		// TODO Auto-generated method stub
		target.setIsoCode(source.getIsoCode());
		target.setRegion(source.getRegion());
	}

}
