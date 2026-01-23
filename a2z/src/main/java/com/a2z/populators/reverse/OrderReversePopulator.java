package com.a2z.populators.reverse;


import com.a2z.enums.OrderType;
import com.a2z.services.interfaces.DeliveryService;
import com.a2z.services.interfaces.OrderService;
import com.a2z.services.interfaces.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.A2zOrder;
import com.a2z.enums.DeliveryMode;
import com.a2z.enums.OrderStatus;
import com.a2z.data.OrderData;
import com.a2z.persistence.A2zPriceRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.populators.Populator;

public class OrderReversePopulator implements Populator<OrderData,A2zOrder>{

	@Autowired
	PODCustomerRepository customerRepo;
	
	@Autowired
	A2zPriceRepository a2zPriceRepository;


	@Autowired
	A2zPriceRepository priceRepo;
	
	@Autowired
	OrderService orderService;
	@Autowired
	DeliveryService deliveryService;
	@Autowired
	PaymentService paymentService;
	
	@Override
	public void populate(OrderData source, A2zOrder target) throws ConversionException {
		String userName = source.getCustomer().getUserName();
		target.setCustomer(customerRepo.findById(userName).get());
		target.setDeliveryAddress(deliveryService.saveDeliveryAddress(source));
		target.setDeliveryMode(DeliveryMode.valueOf(source.getDeliveryMode()));
		target.setEntries(orderService.saveEntries(source));
		if(!source.getExtendedOrder())
		    target.setId(source.getId());
		target.setNamedDeliveryDate(source.getNamedDeliveryDate());
		target.setPaymentInfo(paymentService.savePaymentInfo(source));
		if(!source.getExtendedOrder()) {
			target.setPrice(priceRepo.findById(source.getPrice().getId()).get());
		}
		target.setStatus(OrderStatus.REVIEW);
		target.setOrderType(OrderType.valueOf(source.getOrderType().toUpperCase()));
		
	}

	
}
