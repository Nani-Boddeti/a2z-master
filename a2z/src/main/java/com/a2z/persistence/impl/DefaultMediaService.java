package com.a2z.persistence.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.a2z.dao.A2zMedia;
import com.a2z.dao.MediaContainer;
import com.a2z.data.MediaContainerData;
import com.a2z.persistence.A2zMediaContainerRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.populators.MediaContainerPopulator;

import io.micrometer.common.util.StringUtils;

@Service
public class DefaultMediaService {

	public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/data/uploads";
	
	@Autowired
	MediaContainerPopulator mediaConatinerPopulator;
	
	@Autowired
	RootRepository rootRepo;
	
	
	@Autowired
	A2zMediaContainerRepository mediaContainerRepository;
	
	public List<MediaContainerData> getAllMedia(){
		List<MediaContainerData> mediaContainerList = new ArrayList<MediaContainerData>();
		Iterable<MediaContainer> mediaEntityList = mediaContainerRepository.findAll();
		mediaEntityList.forEach(container->{
			MediaContainerData mediaContainerData = new MediaContainerData();
			mediaConatinerPopulator.populate(container, mediaContainerData);
			mediaContainerList.add(mediaContainerData);
		});
		
		return mediaContainerList;
		
	}
	
	public MediaContainerData uploadMedia(String userName, MultipartFile[] files, boolean isMap) {
		final MediaContainer container = new MediaContainer();
			container.setUserId(userName);
		mediaContainerRepository.save(container);
		List<A2zMedia> uploadedMediaList = new ArrayList<A2zMedia>();
		Arrays.stream(files).forEach(file->{
			A2zMedia media = new A2zMedia();
			StringBuilder fileNames = new StringBuilder();
			Path fileNameDirectory = Paths.get(UPLOAD_DIRECTORY.concat("/").concat(container.getCode()));
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY.concat("/").concat(container.getCode()), file.getOriginalFilename());
            fileNames.append(file.getOriginalFilename());
            try {
                media.setFileName(file.getOriginalFilename());
                media.setAbsolutePath(fileNameAndPath.toString());
                media.setMap(isMap);
                media.setSize(file.getSize());
                media.setMime(Files.probeContentType(fileNameAndPath));
                uploadedMediaList.add(media);
                if(!Files.exists(fileNameDirectory)) {
                	new File(fileNameDirectory.toString()).mkdirs();
                }
				Files.write(fileNameAndPath, file.getBytes());
				media.setMediaContainer(container);
				rootRepo.save(media);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
		Set<A2zMedia> allMedias = new HashSet<A2zMedia>();
		if(container != null && container.getMedias()!=null)
		allMedias.addAll(container.getMedias());
		allMedias.addAll(uploadedMediaList);
		container.setMedias(new ArrayList<>(allMedias));
		mediaContainerRepository.save(container);
		MediaContainerData mediaContainerData  = new MediaContainerData();
		mediaConatinerPopulator.populate(container, mediaContainerData);
		return mediaContainerData;
	}
	
	public void deleteMediaContainer(String code){
		Optional<MediaContainer> mediaContainerOpt = mediaContainerRepository.findById(code);
		if(mediaContainerOpt.isPresent()) {
			mediaContainerRepository.delete(mediaContainerOpt.get());
		}
	}
	
	private MediaContainer isMediaContainerPresent(String code) {
		Optional<MediaContainer> mediaContainerOpt = mediaContainerRepository.findById(code);
		if(mediaContainerOpt.isPresent()) {
			return mediaContainerOpt.get();
		}
		return new MediaContainer();
	}
	
	public MediaContainerData getProofMedia(String userName) {
		Optional<MediaContainer> mediaContainerOpt = mediaContainerRepository.findById(userName+"-proofs");
		MediaContainerData mediaContainerData = new MediaContainerData();
		if(mediaContainerOpt.isPresent()) {
			mediaConatinerPopulator.populate(mediaContainerOpt.get(), mediaContainerData);
		}
		return mediaContainerData;
	}
}
