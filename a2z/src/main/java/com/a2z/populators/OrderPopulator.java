package com.a2z.populators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.A2zOrder;
import com.a2z.data.AddressData;
import com.a2z.data.CustomerData;
import com.a2z.data.OrderData;
import com.a2z.data.OrderEntryData;
import com.a2z.data.PaymentInfoData;
import com.a2z.data.PriceData;

public class OrderPopulator implements Populator<A2zOrder,OrderData>{

	@Autowired
	CustomerPopulator customerPopulator;
	
	@Autowired
	AddressPopulator addressPopulator;
	
	@Autowired
	PaymentInfoPopulator paymentInfoPopulator;
	
	@Autowired
	PricePopulator pricePopulator;
	
	@Autowired
	OrderEntryPopulator orderEntryPopulator;

	
	@Override
	public void populate(A2zOrder source, OrderData target) throws ConversionException {
		CustomerData customerData = new CustomerData();
		customerPopulator.populate(source.getCustomer(), customerData);
		target.setCustomer(customerData);
		AddressData deliveryAddress = new AddressData();
		addressPopulator.populate(source.getDeliveryAddress(), deliveryAddress);
		target.setDeliveryAddress(deliveryAddress);
		target.setDeliveryMode(source.getDeliveryMode().name());
		List<OrderEntryData> entriesList = new ArrayList<OrderEntryData>();
		source.getEntries().forEach(entry->{
			OrderEntryData orderEntryData = new OrderEntryData();
			orderEntryPopulator.populate(entry, orderEntryData);
			entriesList.add(orderEntryData);
		});
		target.setEntries(entriesList);
		target.setId(source.getId());
		target.setNamedDeliveryDate(source.getNamedDeliveryDate());
		PaymentInfoData paymentInfoData = new PaymentInfoData();
		paymentInfoPopulator.populate(source.getPaymentInfo(), paymentInfoData);
		target.setPaymentInfo(paymentInfoData);
		PriceData priceData = new PriceData();
		pricePopulator.populate(source.getPrice(), priceData);
		target.setPrice(priceData);
		target.setStatus(source.getStatus().name());
	}

}
