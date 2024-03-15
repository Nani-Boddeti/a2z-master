package com.a2z.search.dao;


public class A2zMediaSearch {

	private String fileName;
	private String latitude;
	private String longitude;
	private String absolutePath;
	private String mime;
	private Long size;

	private String mediaContainerCode;
	private boolean isMap ;
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
	public String getMediaContainerCode() {
		return mediaContainerCode;
	}
	public void setMediaContainerCode(String mediaContainerCode) {
		this.mediaContainerCode = mediaContainerCode;
	}
	public boolean isMap() {
		return isMap;
	}
	public void setMap(boolean isMap) {
		this.isMap = isMap;
	}
}
