package com.a2z.populators;

import org.springframework.core.convert.ConversionException;

import com.a2z.dao.A2zAddress;
import com.a2z.data.AddressData;

public class AddressPopulator implements Populator<A2zAddress,AddressData>{
	
	@Override
	public void populate(A2zAddress source, AddressData target) throws ConversionException {
		// TODO Auto-generated method stub
		target.setApartment(source.getApartment());
		target.setBuilding(source.getBuilding());
		target.setCellphone(source.getCellphone());
		target.setCompany(source.getCellphone());
		target.setCustomer(source.getCustomer().getUserName());
		target.setDistrict(source.getDistrict());
		target.setEmail(source.getEmail());
		target.setFirstName(source.getFirstName());
		target.setId(source.getId());
		target.setLastName(source.getLastName());
		target.setLine1(source.getLine1());
		target.setLine2(source.getLine2());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
		target.setDefaultAddress(source.isDefaultAddress());
	}

}
