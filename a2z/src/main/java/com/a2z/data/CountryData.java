package com.a2z.data;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*@Validated*/
public class CountryData extends RootData {

	@Pattern(regexp="^[a-zA-Z[ ]*]*")
	@Size(min = 2, message = "{validation.isoCode.size.too_short}")
	@Size(max = 10, message = "{validation.isoCode.size.too_long}")
	private String isoCode;
	@Pattern(regexp="^[a-zA-Z[ ]*]*")
	@Size(min = 2, message = "{validation.region.size.too_short}")
	@Size(max = 10, message = "{validation.region.size.too_long}")
	private String region;
	public String getIsoCode() {
		return isoCode;
	}
	public void setIsoCode(String isoCode) {
		this.isoCode = isoCode;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
}
