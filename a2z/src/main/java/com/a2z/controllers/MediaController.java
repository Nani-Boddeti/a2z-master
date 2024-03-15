package com.a2z.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.a2z.dao.A2zMedia;
import com.a2z.dao.MediaContainer;
import com.a2z.data.MediaContainerData;
import com.a2z.persistence.impl.DefaultAdPostService;
import com.a2z.persistence.impl.DefaultMediaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@ResponseBody
@RequestMapping("/media")
@Secured("SCOPE_app.read")
@Validated
public class MediaController extends RootController {

	@Autowired
	DefaultAdPostService adPostService;
	
	@Autowired
	DefaultMediaService mediaService;
	

	@PostMapping("/upload") public MediaContainerData uploadImage(@RequestPart("files") MultipartFile[] files, HttpServletRequest request,@RequestParam @Valid final boolean isMap) throws IOException {
		String userName = getSessionUserName();
		if(StringUtils.isNotEmpty(userName))
        return mediaService.uploadMedia(userName, files, isMap);
		return new MediaContainerData();
    }
	
	@GetMapping("/get") public List<MediaContainerData> getAllMedia(HttpServletRequest request) {
		if(isSessionValid())
		return mediaService.getAllMedia();
		return new ArrayList<MediaContainerData>();
	}
	
	/*
	 * private void classifyImage(String path) { Mat image = Imgcodecs.imread(path);
	 * Mat grayImage = new Mat(); Imgproc.cvtColor(image, grayImage,
	 * Imgproc.COLOR_BGR2GRAY); CascadeClassifier faceCascade = new
	 * CascadeClassifier("path/to/haarcascade_frontalface_alt.xml"); MatOfRect faces
	 * = new MatOfRect(); faceCascade.detectMultiScale(grayImage, faces);
	 * 
	 * List<Rect> faceList = new ArrayList<>(faces.toList()); for (Rect face :
	 * faceList) { Imgproc.rectangle(image, face, new Scalar(0, 255, 0), 3); }
	 * 
	 * Imgcodecs.imwrite("path/to/save/detected_faces.jpg", image); }
	 */
		
	@GetMapping("/delete/{code}") public void deleteMediaContainer(@PathVariable @Valid String code , HttpServletRequest request) {
		String userName = getSessionUserName();
		/*if(StringUtils.isEmpty(userName)) {
			return Collections.unmodifiableSet(Collections.singleton("User Session Ended.")) ;
		}*/
		if(StringUtils.isNotEmpty(userName))
		mediaService.deleteMediaContainer(userName);
	}
}
