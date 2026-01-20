package com.a2z.populators.reverse;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.Country;
import com.a2z.dao.Customer;
import com.a2z.dao.PaymentInfo;
import com.a2z.data.CustomerData;
import com.a2z.persistence.A2zCountryRepository;
import com.a2z.populators.Populator;


public class CustomerReversePopulator implements Populator<CustomerData,Customer>{

	@Autowired
	CountryReversePopulator countryReversePopulator;
	
	@Autowired
	PaymentInfoBasicReversePopulator paymentInfoBasicReversePopulator;
	
	@Autowired
	A2zCountryRepository countryRepo;
	
	@Override
	public void populate(CustomerData source, Customer target) throws ConversionException {
		
		String countryIso = source.getDefaultCountry() != null ? source.getDefaultCountry().getIsoCode():StringUtils.EMPTY;
		if(StringUtils.isNotEmpty(countryIso)) {
			Optional<Country> countryOpt = countryRepo.findById(countryIso);
			if(countryOpt.isPresent()) {
				target.setDefaultCountry(countryOpt.get());
			}
			else {
				Country country = new Country();
				countryReversePopulator.populate(source.getDefaultCountry(), country);	
				target.setDefaultCountry(country);
			}
		}
		PaymentInfo paymentInfo = new PaymentInfo();
		if(source.getDefaultPaymentInfo() != null)
		paymentInfoBasicReversePopulator.populate(source.getDefaultPaymentInfo(), paymentInfo);				
		target.setDefaultPaymentInfo(paymentInfo);
		target.setEmail(source.getEmail());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setTitle(source.getTitle());
		target.setPhoneNumber(source.getPhoneNumber());
	}

}
