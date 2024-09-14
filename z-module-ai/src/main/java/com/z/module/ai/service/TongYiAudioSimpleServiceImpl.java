package com.z.module.ai.service;

import com.alibaba.dashscope.audio.tts.SpeechSynthesisAudioFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
//@Service
public class TongYiAudioSimpleServiceImpl extends AbstractTongYiServiceImpl {
//    private final SpeechClient speechClient;

//    @Autowired
//    public TongYiAudioSimpleServiceImpl(SpeechClient client) {
//        this.speechClient = client;
//    }

    @Override
    public String genAudio(String text) {
        log.info("gen audio prompt is: {}", text);
//        var resWAV = speechClient.call(text);
        // save的代码省略，就是将音频保存到本地而已
//        return save(resWAV, SpeechSynthesisAudioFormat.WAV.getValue());
        return null;
    }
}