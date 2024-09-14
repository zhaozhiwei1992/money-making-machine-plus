package com.z.module.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Service;

@Slf4j
//@Service
public class TongYiImagesServiceImpl extends AbstractTongYiServiceImpl {

//    private final ImageClient imageClient;

    public ImageResponse genImg(String imgPrompt) {
        ImagePrompt prompt = new ImagePrompt(imgPrompt);
//       return imageClient.call(prompt);
        return null;
    }
}