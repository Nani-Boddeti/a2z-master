package com.a2z.services.interfaces;

import com.a2z.dao.A2zOrder;
import com.a2z.dao.OrderEntry;
import com.a2z.data.OrderData;

import java.util.List;

public interface OrderService {

    void submitOrder(A2zOrder order, boolean isExtended, A2zOrder originalOrder);

    OrderData getOrderDetail(String userName, Long id);

    boolean isCustomerEligibleToOrder(String userName);


     List<OrderEntry> saveEntries(OrderData orderData);

}
