package com.a2z.populators;

import com.a2z.dao.AdPost;
import com.a2z.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

public class AdPostCategoryPopulator implements Populator<AdPost, AdPostData> {


    @Autowired
    MediaContainerPopulator mediaContainerPopulator;

    @Autowired
    PricePopulator pricePopulator;


    @Override
    public void populate(AdPost source, AdPostData target) throws ConversionException {
        target.setDescription(source.getDescription());
        target.setId(source.getId());
        MediaContainerData mediaContainerData = new MediaContainerData();
        mediaContainerPopulator.populate(source.getMediaContainer(), mediaContainerData);
        target.setMediaContainerData(mediaContainerData);
        PriceData priceData = new PriceData();
        pricePopulator.populate(source.getPrice(), priceData);
        target.setPrice(priceData);
        target.setActive(source.isActive());
        target.setProductName(source.getProductName());
        if(source.getSourceAddress()!= null) {
            target.setLatitude(source.getSourceAddress().getLatitude());
            target.setLongitude(source.getSourceAddress().getLongitude());
        }

    }
}
