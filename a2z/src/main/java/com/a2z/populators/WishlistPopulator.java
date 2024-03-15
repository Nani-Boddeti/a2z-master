package com.a2z.populators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.A2zWishlist;
import com.a2z.data.AdPostData;
import com.a2z.data.WishlistData;

public class WishlistPopulator implements Populator<A2zWishlist,WishlistData>{

	@Autowired
	AdPostPopulator adPostPopulator;
	
	@Override
	public void populate(A2zWishlist source, WishlistData target) throws ConversionException {
		List<AdPostData> adList = new ArrayList<AdPostData>();
		source.getAds().stream().forEach(ad->{
			AdPostData adData = new AdPostData();
			adPostPopulator.populate(ad, adData);
			adList.add(adData);
		});
		target.setAds(adList);
	}

}
