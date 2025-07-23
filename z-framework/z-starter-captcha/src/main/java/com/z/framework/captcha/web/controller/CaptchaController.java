package com.z.framework.captcha.web.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Title: CaptchaController
 * @Package com/longtu/web/controller/CaptchaController.java
 * @Description: 获取验证码
 * @author zhaozhiwei
 * @date 2022/8/31 上午9:35
 * @version V1.0
 */
@Controller
@Slf4j
public class CaptchaController {

    private final Producer captchaProducer;

    public CaptchaController(Producer captchaProducer) {
        this.captchaProducer = captchaProducer;
    }

    /**
     * @data: 2022/8/31-上午10:09
     * @User: zhaozhiwei
     * @method: getCaptchaCode
      * @param request :
 * @param response :
     * @return: org.springframework.web.servlet.ModelAndView
     * @Description: 数字验证码
     */
    @RequestMapping("/captcha/numCode")
    public void getCaptchaCode(HttpServletRequest request, HttpServletResponse response) throws IOException{
        HttpSession session = request.getSession();

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        //生成验证码文本
        String capText = captchaProducer.createText();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        log.info("生成验证码文本===="+capText);
        //利用生成的字符串构建图片
        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();

        try (out) {
            ImageIO.write(bi, "jpg", out);
            out.flush();
        }
    }
}
