package com.a2z.populators.reverse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.PaymentInfo;
import com.a2z.data.PaymentInfoData;
import com.a2z.persistence.impl.ExtendedOrderService;
import com.a2z.populators.Populator;

public class PaymentInfoBasicReversePopulator implements Populator<PaymentInfoData,PaymentInfo>{

	@Autowired
	ExtendedOrderService extendedOrderService;
	
	@Override
	public void populate(PaymentInfoData source, PaymentInfo target) throws ConversionException {
		// TODO Auto-generated method stub
		target.setPaymentCode(source.getPaymentCode());
		target.setPaymentType(source.getPaymentType());
	}
}
