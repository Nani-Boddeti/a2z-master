package com.a2z.populators;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.PaymentInfo;
import com.a2z.data.AddressData;
import com.a2z.data.PaymentInfoData;

public class PaymentInfoPopulator implements Populator<PaymentInfo,PaymentInfoData>{

	@Autowired
	AddressPopulator addressPopulator;
	
	@Override
	public void populate(PaymentInfo source, PaymentInfoData target) throws ConversionException {
		// TODO Auto-generated method stub
		target.setPaymentCode(source.getPaymentCode());
		target.setPaymentType(source.getPaymentType());
		AddressData paymentAddress = new AddressData();
		if(Objects.nonNull(source.getPaymentAddress()))
		addressPopulator.populate(source.getPaymentAddress(), paymentAddress);
		target.setPaymentAddress(paymentAddress);
	}


	

}
