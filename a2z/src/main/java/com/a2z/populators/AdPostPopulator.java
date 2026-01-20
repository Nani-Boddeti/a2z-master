package com.a2z.populators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.AdPost;
import com.a2z.data.AdPostData;
import com.a2z.data.AddressData;
import com.a2z.data.CustomerData;
import com.a2z.data.MediaContainerData;
import com.a2z.data.PriceData;

public class AdPostPopulator implements Populator<AdPost,AdPostData>{

	@Autowired
	CustomerPopulator customerPopulator;
	
	@Autowired
	MediaContainerPopulator mediaContainerPopulator;
	
	@Autowired
	PricePopulator pricePopulator;
	
	@Autowired
	AddressPopulator addressPopulator;
	
	@Override
	public void populate(AdPost source, AdPostData target) throws ConversionException {
		target.setDescription(source.getDescription());
		target.setId(source.getId());
		CustomerData customerData = new CustomerData();
		customerPopulator.populate(source.getCustomer(), customerData);
		target.setCustomer(customerData);
		MediaContainerData mediaContainerData = new MediaContainerData();
		mediaContainerPopulator.populate(source.getMediaContainer(), mediaContainerData);
		target.setMediaContainerData(mediaContainerData);
		PriceData priceData = new PriceData();
		pricePopulator.populate(source.getPrice(), priceData);
		target.setPrice(priceData);
		target.setActive(source.isActive());
		target.setProductName(source.getProductName());
		if(source.getSourceAddress()!= null) {
			AddressData addressData = new AddressData();
			addressPopulator.populate(source.getSourceAddress(),addressData);
			target.setSourceAddress(addressData);
		}
		
	}

}
