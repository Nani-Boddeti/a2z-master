package com.a2z.eventlisteners;

import com.a2z.dao.A2zMedia;
import com.a2z.dao.AdPost;
import com.a2z.dao.Customer;
import com.a2z.events.AdPostSubmissionEvent;
import com.a2z.events.CustomerRegistrationEvent;
import com.a2z.services.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Async
public class DefaultEventListener {

    @Value("${media.server.url}")
    private String mediaBaseUrl;
    @Value("${custom.application.server.url}")
    private String applicationServerUrl;

    @Autowired
    EmailService emailService;

    public String getMediaContainercode() {
        return mediaContainercode;
    }

    public void setMediaContainercode(String mediaContainercode) {
        this.mediaContainercode = mediaContainercode;
    }

    private String mediaContainercode ;

    @EventListener
    public void onApplicationEvent(AdPostSubmissionEvent event) {
        // Handle the event (e.g., send notification email, log activity, etc.)
        AdPost adPost = (AdPost) event.getSource();
        String userName = event.getUserName();
        String adTitle = event.getAdTitle();
        this.mediaContainercode = event.getMediaContainerCode();
        Map<String, Object> model = getModel(event, userName, adTitle,adPost);
        String subjectPath = "/templates/adPostEmail/subject.vm";
        String bodyPath = "/templates/adPostEmail/body.vm";
        emailService.sendTemplateEmail(event.getToAddress(), subjectPath, bodyPath, model);

    }
    private String prepareImageUrl(String fileName) {
        return this.mediaBaseUrl + this.getMediaContainercode() + "/" + fileName;
    }
    @EventListener
    public void onCustomerRegistrationEvent(CustomerRegistrationEvent event) {
        // Handle the event (e.g., send notification email, log activity, etc.)
        Customer customer = event.getCustomer();
        Map<String, Object> model = getCustomerContext(customer);
        String subjectPath = "/templates/customerRegistration/subject.vm";
        String bodyPath = "/templates/customerRegistration/body.vm";
        emailService.sendTemplateEmail(customer.getEmail(), subjectPath, bodyPath, model);

    }
    private Map<String, Object> getModel(AdPostSubmissionEvent event, String userName, String adTitle, AdPost adPost) {
        List<String> imageUrls = (ArrayList)adPost.getMediaContainer().getMedias().stream()
                .map(A2zMedia::getFileName).map(this::prepareImageUrl).collect(Collectors.toList());
        String adDescription = event.getAdDescription();
        String adPrice = event.getAdPrice();
        String postDate = event.getPostDate();
        String adLink = this.applicationServerUrl+"/ad-details?adId="+event.getAdId();
        Map<String,Object> contextMap = new HashMap<>();
        contextMap.put("userName", userName);
        contextMap.put("adTitle", adTitle);
        contextMap.put("adDescription", adDescription);
        contextMap.put("adImageUrls", imageUrls);
        contextMap.put("adPrice", adPrice);
        contextMap.put("postDate", postDate);
        contextMap.put("adLink", adLink);
        return contextMap;
    }
    private Map<String, Object> getCustomerContext(Customer customer) {
        Map<String,Object> contextMap = new HashMap<>();
        contextMap.put("userName", customer.getUserName());
        contextMap.put("title", "MR.");
        contextMap.put("fullName", customer.getFirstName() + " " + customer.getLastName());
        contextMap.put("email", customer.getEmail());
        contextMap.put("phoneNumber", customer.getPhoneNumber());
        contextMap.put("creationDate", customer.getCreatedDate().toString());
        return contextMap;
    }


}
