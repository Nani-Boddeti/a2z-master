package com.a2z.populators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionException;

import com.a2z.dao.MediaContainer;
import com.a2z.data.MediaContainerData;
import com.a2z.data.MediaData;


public class MediaContainerPopulator implements Populator<MediaContainer,MediaContainerData>{
	@Autowired
	private MediaPopulator mediaPopulator;
	
	@Override
	public void populate(MediaContainer source, MediaContainerData target) throws ConversionException {
		
		target.setCode(source.getCode());
		List<MediaData> mediaDataList = new ArrayList<MediaData>();
				source.getMedias().stream().forEach(media->{
			MediaData mediaData = new MediaData();
			getMediaPopulator().populate(media, mediaData);
			mediaDataList.add(mediaData);
		});
		target.setMedias(mediaDataList);
	}

	public MediaPopulator getMediaPopulator() {
		return mediaPopulator;
	}

	public void setMediaPopulator(MediaPopulator mediaPopulator) {
		this.mediaPopulator = mediaPopulator;
	}

}
