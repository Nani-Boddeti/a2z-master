package com.a2z.services.impl;

import com.a2z.dao.A2zAddress;
import com.a2z.dao.AdPost;
import com.a2z.data.OrderData;
import com.a2z.data.OrderEntryData;
import com.a2z.persistence.A2zAdPostRepository;
import com.a2z.services.interfaces.AddressService;
import com.a2z.services.interfaces.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultDeliveryService implements DeliveryService {
    @Autowired
    AddressService addressService;
    @Autowired
    A2zAdPostRepository adPostRepo;


    @Override
    public A2zAddress saveDeliveryAddress(OrderData orderData) {
        Optional<OrderEntryData> entryDataOpt = orderData.getEntries().stream().findFirst();
        A2zAddress target = new A2zAddress();
        if(entryDataOpt.isPresent()) {
            Optional<AdPost> adPostOpt = adPostRepo.findById(entryDataOpt.get().getAdPost().getId());
            if(adPostOpt.isPresent()) {
                addressService.cloneAddressModel(adPostOpt.get().getSourceAddress(),target);
            }
        }
        return addressService.saveAddress(target, true , false);

    }
}
