package com.a2z.dao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Entity
public class A2zMedia extends RootEntity {
	

	private String fileName;
	private String latitude;
	private String longitude;
	private String absolutePath;
	private String mime;
	private Long size;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="mediaContainer_code", referencedColumnName = "code")
	private MediaContainer mediaContainer;
	private boolean isMap ;
	
	public MediaContainer getMediaContainer() {
		return mediaContainer;
	}
	public void setMediaContainer(MediaContainer mediaContainer) {
		this.mediaContainer = mediaContainer;
	}
	public boolean isMap() {
		return isMap;
	}
	public void setMap(boolean isMap) {
		this.isMap = isMap;
	}
	public String getMime() {
		return mime;
	}
	public void setMime(String mime) {
		this.mime = mime;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAbsolutePath() {
		return absolutePath;
	}
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

}
