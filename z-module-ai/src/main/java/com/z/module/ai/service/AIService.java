package com.z.module.ai.service;

import org.springframework.ai.image.ImageResponse;

/**
 * @Title: AIService
 * @Package com/z/module/ai/service/AIService.java
 * @Description: AI
 * @author zhaozhiwei
 * @date 2025/3/29 11:04
 * @version V1.0
 */
public interface AIService {

    /**
     * 基本问答
     */
    String completion(String message);
    /**
     * 文生图
     */
    ImageResponse genImg(String imgPrompt);

    /**
     * 语音合成
     */
    String genAudio(String text);

}