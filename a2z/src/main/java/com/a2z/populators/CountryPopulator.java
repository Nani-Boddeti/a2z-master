package com.a2z.populators;

import org.springframework.core.convert.ConversionException;

import com.a2z.dao.Country;
import com.a2z.data.CountryData;




public class CountryPopulator implements Populator<Country,CountryData>{

	@Override
	public void populate(Country source, CountryData target) throws ConversionException {
		// TODO Auto-generated method stub
		target.setIsoCode(source.getIsoCode());
		target.setRegion(source.getRegion());
	}


}
