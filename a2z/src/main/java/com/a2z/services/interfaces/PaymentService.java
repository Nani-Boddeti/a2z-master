package com.a2z.services.interfaces;

import com.a2z.dao.A2zAddress;
import com.a2z.dao.Customer;
import com.a2z.dao.PaymentInfo;
import com.a2z.dao.UserGroup;
import com.a2z.data.OrderData;

public interface PaymentService {
    void setPrimeGroup(Customer customer);

    void createPrimeUser(Customer customer, UserGroup primeUserGroup);

    A2zAddress savePaymentAddress(OrderData orderData);

    PaymentInfo savePaymentInfo(OrderData orderData);
}
