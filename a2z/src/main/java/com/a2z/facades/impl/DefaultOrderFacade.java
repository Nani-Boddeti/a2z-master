package com.a2z.facades.impl;

import com.a2z.dao.*;
import com.a2z.data.OrderData;
import com.a2z.facades.OrderFacade;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.populators.OrderPopulator;
import com.a2z.populators.reverse.OrderReversePopulator;
import com.a2z.services.interfaces.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultOrderFacade implements OrderFacade {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderReversePopulator orderReversePopulator;

    @Autowired
    OrderPopulator orderPopulator;

    @Autowired
    PODCustomerRepository customerRepo;

    @Autowired
    RootRepository rootRepo;


    @Override
    public OrderData submitOrder(OrderData orderData, String userName, boolean isExtended, A2zOrder originalOrder) {
        if (orderService.isCustomerEligibleToOrder(userName)) {
            A2zOrder order = populateOrder(orderData, userName);
            orderService.submitOrder(order,isExtended, originalOrder);
            orderPopulator.populate(order, orderData);
        } else {
            orderData.setErrorMessage("Customer is not eligible to order.");
        }
        return orderData;
    }

    @Override
    public OrderData returnOrExtend(String userName, Long id, boolean isReturned, boolean isExtend){
        OrderData orderData = new OrderData();
        if(StringUtils.isNotEmpty(userName)) {
            Optional<Customer> customerOpt = customerRepo.findById(userName);
            if(customerOpt.isPresent()) {
                Optional<A2zOrder> orderOpt =  rootRepo.getOrderDetails(id, customerOpt.get());
                if(orderOpt.isPresent()) {
                    A2zOrder order = orderOpt.get();
                    orderPopulator.populate(order, orderData);
                    if(isReturned) {
                        order.setStatus(OrderStatus.RETURNED);
                        orderData.setStatus(OrderStatus.RETURNED.toString());
                        rootRepo.save(order);
                    } else {
                        this.submitOrder(orderData,userName, isExtend, order);
                    }
                }
            }
        }
        return orderData;
    }

    private A2zOrder populateOrder(OrderData orderData, String userName) {

        orderData.getCustomer().setUserName(userName);
        A2zOrder order = new A2zOrder();
        orderReversePopulator.populate(orderData, order);
        return order;
    }
}
