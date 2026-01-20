package com.a2z.populators.reverse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.A2zAddress;
import com.a2z.data.AddressData;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.populators.Populator;

public class AddressReversePopulator implements Populator<AddressData , A2zAddress>{

	@Autowired
	PODCustomerRepository customerRepo;
	
	
	@Override
	public void populate(AddressData source, A2zAddress target) throws ConversionException {
		target.setAppartment(source.getAppartment());
		target.setBuilding(source.getBuilding());
		target.setCellphone(source.getCellphone());
		target.setCompany(source.getCellphone());	
		target.setCustomer(customerRepo.findById(source.getCustomer()).get());
		target.setDistrict(source.getDistrict());
		target.setEmail(source.getEmail());
		target.setFirstName(source.getFirstName());
		target.setId(source.getId());
		target.setLastName(source.getLastName());
		target.setLine1(source.getLine1());
		target.setLine2(source.getLine2());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
	}

}
