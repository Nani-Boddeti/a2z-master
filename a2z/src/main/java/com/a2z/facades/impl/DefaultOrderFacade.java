package com.a2z.facades.impl;

import com.a2z.dao.*;
import com.a2z.data.AdPostData;
import com.a2z.data.OrderData;
import com.a2z.data.OrderEntryData;
import com.a2z.enums.OrderStatus;
import com.a2z.enums.OrderType;
import com.a2z.facades.OrderFacade;
import com.a2z.persistence.A2zPriceRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.populators.OrderPopulator;
import com.a2z.populators.reverse.OrderReversePopulator;
import com.a2z.services.interfaces.AdPostService;
import com.a2z.services.interfaces.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    A2zPriceRepository a2zPriceRepository;

    @Autowired
    AdPostService adPostService;

    @Override
    public OrderData submitOrder(OrderData orderData, String userName, boolean isExtended, A2zOrder originalOrder) {
        if (orderService.isCustomerEligibleToOrder(userName) && !isUserAndOwnerSame(userName, orderData)) {
            orderData.setExtendedOrder(isExtended);
            A2zOrder order = populateOrder(orderData, userName);
            orderService.submitOrder(order,isExtended, originalOrder);
            orderPopulator.populate(order, orderData);
        } else {
            orderData.setErrorMessage("Customer is not eligible to order.");
        }
        return orderData;
    }
    private boolean isUserAndOwnerSame(String userName, OrderData orderData) {
          Long adPostId = Optional.ofNullable(orderData)
                  .map(OrderData::getEntries)
                  .filter(entries -> !entries.isEmpty())
                  .map(entries -> entries.get(0))
                  .map(OrderEntryData::getAdPost)
                  .map(AdPostData::getId)
                  .orElse(null);
          if(adPostId != null){
              Optional<AdPost> adPostOpt = adPostService.getAdEntityById(adPostId);
              if(adPostOpt.isPresent()) {
                  AdPost adPost = adPostOpt.get();
                  return StringUtils.equals(adPost.getCustomer().getUserName(), userName);
              }
          }

      return false;

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
        if(orderData.getExtendedOrder()){
            Price price = new Price();
            price.setAmount(orderData.getPrice().getAmount());
            price.setCurrency(orderData.getPrice().getCurrency());
            a2zPriceRepository.save(price);
            order.setPrice(price);
        }
        orderReversePopulator.populate(orderData, order);
        return order;
    }

    @Override
    public List<String> getAllOrderTypes() {
        List<String> orderTypeList = new ArrayList<>();
        for(OrderType type : OrderType.values()) {
            orderTypeList.add(type.toString());
        }
        return orderTypeList;
    }
    @Override
    public List<String> getAllOrderStatuses() {
        List<String> orderStatusList = new ArrayList<>();
        for(OrderStatus type : OrderStatus.values()) {
            orderStatusList.add(type.toString());
        }
        return orderStatusList;
    }
}
