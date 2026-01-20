package com.a2z.populators;

import com.a2z.dao.A2zAddress;
import com.a2z.data.AddressData;
import com.a2z.persistence.RootRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.Customer;
import com.a2z.dao.UserGroup;
import com.a2z.data.CountryData;
import com.a2z.data.CustomerData;
import com.a2z.data.PaymentInfoData;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class CustomerPopulator implements Populator<Customer, CustomerData> {

	@Autowired
	CountryPopulator countryPopulator;

	@Autowired
	PaymentInfoBasicPopulator paymentInfoBasicPopulator;

	@Autowired
	RootRepository rootRepository;
	@Autowired
	AddressPopulator addressPopulator;
	@Override
	public void populate(Customer source, CustomerData target) throws ConversionException {
		CountryData countryData = new CountryData();
		countryPopulator.populate(source.getDefaultCountry(), countryData);
		target.setDefaultCountry(countryData);
		PaymentInfoData paymentInfoData = new PaymentInfoData();
		paymentInfoBasicPopulator.populate(source.getDefaultPaymentInfo(), paymentInfoData);
		target.setDefaultPaymentInfo(paymentInfoData);
		target.setEmail(source.getEmail());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setTitle(source.getTitle());
		target.setUserName(source.getUserName());
		target.setPhoneNumber(source.getPhoneNumber());
		target.setUserGroupNames(source.getUserGroups().stream().map(UserGroup::getUserGroupName).toList());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate = formatter.format(source.getCreatedDate());
		target.setCreatedDate(formattedDate);
		AddressData defaultAddressData = new AddressData();
		Optional<A2zAddress> defaultAddressModelOpt = rootRepository.getMyDefaultAddress(source,true);
		if(defaultAddressModelOpt.isPresent())
		addressPopulator.populate(defaultAddressModelOpt.get(), defaultAddressData);
			target.setDefaultAddress(defaultAddressData);

	}

}
