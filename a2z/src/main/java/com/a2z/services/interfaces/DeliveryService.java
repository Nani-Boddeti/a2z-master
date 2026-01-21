package com.a2z.services.interfaces;

import com.a2z.dao.A2zAddress;
import com.a2z.data.OrderData;

public interface DeliveryService {
    A2zAddress saveDeliveryAddress(OrderData orderData);
}
