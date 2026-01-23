package com.a2z.facades;

import com.a2z.dao.A2zOrder;
import com.a2z.data.OrderData;

import java.util.List;

public interface OrderFacade {
    OrderData submitOrder(OrderData orderData, String userName, boolean isExtended, A2zOrder originalOrder);

    OrderData returnOrExtend(String userName, Long id, boolean isReturned, boolean isExtend);

    List<String> getAllOrderTypes();

    List<String> getAllOrderStatuses();
}
