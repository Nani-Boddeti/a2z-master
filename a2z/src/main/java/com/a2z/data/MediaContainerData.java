package com.a2z.data;

import java.util.List;


import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Validated
public class MediaContainerData extends RootData {

	@Pattern(regexp="^[a-zA-Z0-9[ ]*]*[?=_]?[a-zA-Z0-9[ -]*]*")
	@Size(min = 2, message = "{validation.code.size.too_short}")
	@Size(max = 40, message = "{validation.code.size.too_long}")
	private String code;
	@Valid
	private List<MediaData> medias;


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public List<MediaData> getMedias() {
		return medias;
	}


	public void setMedias(List<MediaData> medias) {
		this.medias = medias;
	}
}
