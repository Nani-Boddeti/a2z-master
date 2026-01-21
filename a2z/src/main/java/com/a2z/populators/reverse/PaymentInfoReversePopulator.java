package com.a2z.populators.reverse;

import com.a2z.dao.A2zAddress;
import com.a2z.services.interfaces.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.PaymentInfo;
import com.a2z.data.PaymentInfoData;
import com.a2z.populators.Populator;

public class PaymentInfoReversePopulator implements Populator<PaymentInfoData,PaymentInfo>{

	@Autowired
	PaymentService PaymentService;
	
	@Override
	public void populate(PaymentInfoData source, PaymentInfo target) throws ConversionException {
		// TODO Auto-generated method stub
		target.setPaymentCode(source.getPaymentCode());
		target.setPaymentType(source.getPaymentType());
		if(source.getPaymentAddress()!= null){
			A2zAddress paymentAddress = new A2zAddress();
			//PaymentService.cloneAddressData(source.getPaymentAddress(),paymentAddress);
			//target.setPaymentAddress(PaymentService.savePaymentAddress(source.getPaymentAddress()));
		}

	}

}
