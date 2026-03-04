package com.z.module.ai.service.job.image;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Midjourney 同步 Job：定时拉去 midjourney 绘制状态
 *
 * @author fansili
 */
//@Component
//@Slf4j
//public class AiMidjourneySyncJob implements CustomJobInterface {
//
//    @Resource
//    private AiImageService imageService;
//
//    @Override
//    public String execute() {
//        Integer count = imageService.midjourneySync();
//        log.info("[execute][同步 Midjourney ({}) 个]", count);
//        return String.format("同步 Midjourney %s 个", count);
//    }
//
//}
