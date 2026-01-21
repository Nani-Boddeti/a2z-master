package com.a2z.services.impl;

import com.a2z.dao.A2zAddress;
import com.a2z.persistence.RootRepository;
import com.a2z.services.interfaces.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultAddressService implements AddressService {
    @Autowired
    RootRepository rootRepo;

    @Override
    public A2zAddress cloneAddressModel(A2zAddress source, A2zAddress target) {
        target.setApartment(source.getApartment());
        target.setBuilding(source.getBuilding());
        target.setCellphone(source.getCellphone());
        target.setCompany(source.getCellphone());
        target.setCustomer(source.getCustomer());
        target.setDistrict(source.getDistrict());
        target.setEmail(source.getEmail());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setLine1(source.getLine1());
        target.setLine2(source.getLine2());
        target.setLatitude(source.getLatitude());
        target.setLongitude(source.getLongitude());
        return target;
    }
    @Override
    public A2zAddress saveAddress(A2zAddress address, boolean isDelivery, boolean isPayment) {
        address.setDeliveryAddress(isDelivery);
        address.setPaymentAddress(isPayment);
        rootRepo.save(address);
        return address;
    }
}
