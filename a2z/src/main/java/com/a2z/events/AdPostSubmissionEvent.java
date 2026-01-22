package com.a2z.events;

import com.a2z.dao.A2zMedia;
import com.a2z.dao.AdPost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AdPostSubmissionEvent extends ApplicationEvent {

    private final String userName;
    private final String adTitle;
    private final String adDescription;

    private final String adPrice;
    private final String postDate;
    private final String mediaContainerCode;
    private final String toAddress;
    private final Long adId;
    public AdPostSubmissionEvent(Object source) {
        super(source);
        AdPost adPost = (AdPost) source;
        this.mediaContainerCode = adPost.getMediaContainer().getCode();

        this.userName = adPost.getCustomer().getUserName();
        this.adTitle = adPost.getProductName();
        this.adDescription = adPost.getDescription();

        this.adPrice = adPost.getPrice().getAmount().toString() + " " + adPost.getPrice().getCurrency();
        this.postDate = adPost.getCreationTime().toString();
        this.adId = adPost.getId();
        this.toAddress = adPost.getCustomer().getEmail();

    }



    public String getPostDate() {
        return postDate;
    }

    public String getAdPrice() {
        return adPrice;
    }


    public String getAdDescription() {
        return adDescription;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public String getUserName() {
        return userName;
    }


    public String getMediaContainerCode() {
        return mediaContainerCode;
    }
    public String getToAddress() {
        return toAddress;
    }

    public Long getAdId() {
        return adId;
    }
}
