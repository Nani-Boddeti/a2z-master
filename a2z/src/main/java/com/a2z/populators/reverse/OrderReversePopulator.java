package com.a2z.populators.reverse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.A2zOrder;
import com.a2z.dao.DeliveryMode;
import com.a2z.dao.OrderStatus;
import com.a2z.data.OrderData;
import com.a2z.persistence.A2zPriceRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.impl.ExtendedOrderService;
import com.a2z.populators.Populator;

public class OrderReversePopulator implements Populator<OrderData,A2zOrder>{

	@Autowired
	PODCustomerRepository customerRepo;
	
	@Autowired
	A2zPriceRepository a2zPriceRepository;


	@Autowired
	A2zPriceRepository priceRepo;
	
	@Autowired
	ExtendedOrderService extendedOrderService;
	
	@Override
	public void populate(OrderData source, A2zOrder target) throws ConversionException {
		String userName = source.getCustomer().getUserName();
		target.setCustomer(customerRepo.findById(userName).get());
		target.setDeliveryAddress(extendedOrderService.saveDeliveryAddress(source.getDeliveryAddress()));
		target.setDeliveryMode(DeliveryMode.valueOf(source.getDeliveryMode()));
		target.setEntries(extendedOrderService.saveEntries(source));
		target.setId(source.getId());
		target.setNamedDeliveryDate(source.getNamedDeliveryDate());
		target.setPaymentInfo(extendedOrderService.savePaymentInfo(source));
 		target.setPrice(priceRepo.findById(source.getPrice().getId()).get());
		target.setStatus(OrderStatus.REVIEW);
		
	}

	
}
