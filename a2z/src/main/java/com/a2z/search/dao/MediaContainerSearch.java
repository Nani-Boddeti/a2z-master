package com.a2z.search.dao;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class MediaContainerSearch {
	private String code;
	@Field(type = FieldType.Nested, includeInParent = true)
	private List<A2zMediaSearch> media;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<A2zMediaSearch> getMedia() {
		return media;
	}
	public void setMedia(List<A2zMediaSearch> media) {
		this.media = media;
	}
	
}
