package com.a2z.dao;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class MediaContainer {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String code;
	
	@OneToMany(fetch = FetchType.EAGER , mappedBy="mediaContainer")
	private List<A2zMedia> medias;

	@OneToOne(mappedBy = "mediaContainer")
    private AdPost adPost;
	
	private String userId ;
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = this.userId!=null ? this.userId.concat(code):code;
	}

	public List<A2zMedia> getMedias() {
		return medias;
	}

	public void setMedias(List<A2zMedia> medias) {
		this.medias = medias;
	}

	public AdPost getAdPost() {
		return adPost;
	}

	public void setAdPost(AdPost adPost) {
		this.adPost = adPost;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
