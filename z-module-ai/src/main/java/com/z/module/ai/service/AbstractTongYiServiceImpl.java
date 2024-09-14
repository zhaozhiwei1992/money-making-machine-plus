package com.z.module.ai.service;

import org.springframework.ai.image.ImageResponse;

public class AbstractTongYiServiceImpl implements TongYiService{
    @Override
    public String completion(String message) {
        return "";
    }

    @Override
    public ImageResponse genImg(String imgPrompt) {
        return null;
    }

    @Override
    public String genAudio(String text) {
        return "";
    }
}
