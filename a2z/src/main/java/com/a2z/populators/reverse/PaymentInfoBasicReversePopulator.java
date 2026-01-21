package com.a2z.populators.reverse;

import com.a2z.services.interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.PaymentInfo;
import com.a2z.data.PaymentInfoData;
import com.a2z.populators.Populator;

public class PaymentInfoBasicReversePopulator implements Populator<PaymentInfoData,PaymentInfo>{

	@Autowired
	OrderService orderService;
	
	@Override
	public void populate(PaymentInfoData source, PaymentInfo target) throws ConversionException {
		// TODO Auto-generated method stub
		target.setPaymentCode(source.getPaymentCode());
		target.setPaymentType(source.getPaymentType());
	}
}
