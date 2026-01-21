package com.a2z.services.interfaces;

import com.a2z.data.AdPostData;
import jakarta.transaction.Transactional;

import java.util.List;

public interface AdPostService {
    @Transactional
    AdPostData submitAdPost(AdPostData adPostData, String userName);

    List<AdPostData> retriveAllAds();

    AdPostData getAdById(Long id);

    AdPostData activatePost(Long id);

    AdPostData receivedStatusUpdate(Long id, boolean isReceived, boolean isAcivate);
}
