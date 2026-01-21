package com.a2z.services.interfaces;

import com.a2z.data.MediaContainerData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {
    List<MediaContainerData> getAllMedia();

    MediaContainerData uploadMedia(String userName, MultipartFile[] files, boolean isMap);

    void deleteMediaContainer(String code);

    MediaContainerData getProofMedia(String userName);
}
