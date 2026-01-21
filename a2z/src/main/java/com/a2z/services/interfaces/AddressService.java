package com.a2z.services.interfaces;

import com.a2z.dao.A2zAddress;

public interface AddressService {
    A2zAddress cloneAddressModel(A2zAddress source, A2zAddress target);

    A2zAddress saveAddress(A2zAddress address, boolean isDelivery, boolean isPayment);
}
