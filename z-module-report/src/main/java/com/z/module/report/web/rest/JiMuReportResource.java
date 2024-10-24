package com.z.module.report.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author zhao
 * @version V1.0
 * @Title: JiMuReportResource
 * @Package com/z/module/report/web/resource/JiMuReportResource.java
 * @Description: 处理积木报表bug
 * 1. 图片无法加载, 写入和读取路径不一致, 并且请求url都是错的,觉比了 issue: https://github.com/jeecgboot/JimuReport/issues/1788
 * @date 2023/5/29 下午11:33
 */
@RestController
@Slf4j
public class JiMuReportResource {

    @Value("${jeecg.path.upload}")
    private String uploadPath;

    private static String e(HttpServletRequest var0) {
        String var1 = (String) var0.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String var2 = (String) var0.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return (new AntPathMatcher()).extractPathWithinPattern(var2, var1);
    }

    /**
     * @param var1 :
     * @param var2 :
     * @data: 2023/5/29-下午11:35
     * @User: zhao
     * @method: e
     * @return: void
     * @Description: 反编译积木报表代码, 处理图片报表背景图无法加载bug (1.5.8), 注: 只处理了本地图片
     * org.jeecg.modules.jmreport.desreport.a.a#e(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http
     * .HttpServletResponse)
     */
    @GetMapping({"/jimureport/**"})
    public void e(HttpServletRequest var1, HttpServletResponse var2) {
        var2.setContentType("image/jpeg;charset=utf-8");
        String imgName = e(var1);

        try {
            imgName = URLDecoder.decode(imgName, "utf-8");
        } catch (UnsupportedEncodingException var25) {
            log.error("图片地址[" + imgName + "]decode失败", var25.getMessage());
        }

        BufferedInputStream var4 = null;
        ServletOutputStream var5 = null;

        try {

            String var6 = this.uploadPath;
            String var7 = var6 + File.separator + "jimureport" + File.separator + imgName;
            var4 = new BufferedInputStream(Files.newInputStream(Paths.get(var7)));
            var5 = var2.getOutputStream();
            byte[] var8 = new byte[1024];

            int var9;
            while ((var9 = var4.read(var8)) > 0) {
                var5.write(var8, 0, var9);
            }

            var2.flushBuffer();
        } catch (IOException var26) {
            log.error("预览图片失败" + var26.getMessage(), var26);

            try {
                var2.flushBuffer();
            } catch (IOException var24) {
                var24.printStackTrace();
            }
        } finally {
            if (var4 != null) {
                try {
                    var4.close();
                } catch (IOException var23) {
                    log.error(var23.getMessage(), var23);
                }
            }
            if (var5 != null) {
                try {
                    var5.close();
                } catch (IOException var22) {
                    log.error(var22.getMessage(), var22);
                }
            }
        }
    }
}
