package com.a2z.populators.reverse;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.AdPost;
import com.a2z.dao.Price;
import com.a2z.data.AdPostData;
import com.a2z.persistence.A2zMediaContainerRepository;
import com.a2z.persistence.A2zPriceRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.populators.Populator;

public class AdPostReversePopulator implements Populator<AdPostData , AdPost>{

	
	@Autowired
	PODCustomerRepository customerRepo;
	
	@Autowired
	A2zPriceRepository a2zPriceRepository;

	@Autowired
	A2zMediaContainerRepository mediaRepository;
	
	
	@Override
	public void populate(AdPostData source, AdPost target) throws ConversionException {
		target.setDescription(source.getDescription());
		target.setId(source.getId());
		String customerId = source.getCustomer().getUserName(); 
		target.setCustomer(customerRepo.findById(customerId).get());
		target.setMediaContainer(mediaRepository.findById(source.getMediaContainerData().getCode()).get());
		Price price = new Price();
		price.setAmount(source.getPrice().getAmount());
		price.setCurrency(source.getPrice().getCurrency());
		a2zPriceRepository.save(price);
		target.setPrice(price);
		target.setActive(BooleanUtils.isTrue(source.isActive()) ? true : false);
		target.setProductName(source.getProductName());
		target.setLatitude(source.getLatitude());
		target.setLongitude(source.getLongitude());
	}

}
