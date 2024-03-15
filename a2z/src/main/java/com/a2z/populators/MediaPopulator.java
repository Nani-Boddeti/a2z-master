package com.a2z.populators;

import org.springframework.core.convert.ConversionException;

import com.a2z.dao.A2zMedia;
import com.a2z.data.MediaData;

public class MediaPopulator implements Populator<A2zMedia,MediaData>{

	@Override
	public void populate(A2zMedia source, MediaData target) throws ConversionException {
		target.setAbsolutePath(source.getAbsolutePath());
		target.setFileName(source.getFileName());
		if(source.isMap()) {
			target.setLatitude(source.getLatitude());
			target.setLongitude(source.getLongitude());
		}
		target.setMap(source.isMap());
		target.setMime(source.getMime());
		target.setSize(source.getSize());		
	}

}
