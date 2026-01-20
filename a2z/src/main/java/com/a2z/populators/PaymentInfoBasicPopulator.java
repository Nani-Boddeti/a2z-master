package com.a2z.populators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.PaymentInfo;
import com.a2z.data.PaymentInfoData;

public class PaymentInfoBasicPopulator implements Populator<PaymentInfo,PaymentInfoData>{

	@Autowired
	AddressPopulator addressPopulator;
	
	@Override
	public void populate(PaymentInfo source, PaymentInfoData target) throws ConversionException {
		// TODO Auto-generated method stub
		target.setPaymentCode(source.getPaymentCode());
		target.setPaymentType(source.getPaymentType());
	}
}
