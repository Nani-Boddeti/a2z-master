package com.a2z.populators.reverse;

import com.a2z.dao.A2zCategory;
import com.a2z.enums.OrderType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.A2zAddress;
import com.a2z.dao.AdPost;
import com.a2z.dao.Price;
import com.a2z.data.AdPostData;
import com.a2z.data.AddressData;
import com.a2z.persistence.A2zMediaContainerRepository;
import com.a2z.persistence.A2zPriceRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.populators.AddressPopulator;
import com.a2z.populators.Populator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdPostReversePopulator implements Populator<AdPostData , AdPost>{

	
	@Autowired
	PODCustomerRepository customerRepo;
	
	@Autowired
	A2zPriceRepository a2zPriceRepository;

	@Autowired
	A2zMediaContainerRepository mediaRepository;
	
	@Autowired
	RootRepository rootRepo;
	
	@Autowired
	AddressReversePopulator addressReversePopulator;
	
	
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
		if (source.getSourceAddress() != null)
			{A2zAddress sourceAddress = new A2zAddress();
			addressReversePopulator.populate(source.getSourceAddress(), sourceAddress);
			rootRepo.save(sourceAddress);
			target.setSourceAddress(sourceAddress);
			}
		List<A2zCategory> categoryList = new ArrayList<>();
		if(target.getCategories() != null)
			categoryList.addAll(target.getCategories());
		if(source.getCategoryCode() != null) {

				Optional<A2zCategory> categoryOpt = rootRepo.getCategoryByCode(source.getCategoryCode());

				if(categoryOpt.isPresent()) {
					categoryList.add(categoryOpt.get());
				}

		}
		target.setCategories(categoryList);
		target.setAdPostType(OrderType.valueOf(source.getOrderType().toUpperCase()));
	}

}
