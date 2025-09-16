package com.z.framework.captcha.web.rest;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @Title: CaptchaController
 * @Package com/longtu/web/controller/CaptchaController.java
 * @Description: 获取验证码, 兼容rest和普通请求方式
 * 前端通过blob或者arraybuffer接收
 * @author zhaozhiwei
 * @date 2022/8/31 上午9:35
 * @version V1.0
 */
@RestController
@Slf4j
public class CaptchaResource {

    private final Producer captchaProducer;

    public CaptchaResource(Producer captchaProducer) {
        this.captchaProducer = captchaProducer;
    }

    /**
     * @param request :
     * @data: 2022/8/31-上午10:09
     * @User: zhaozhiwei
     * @method: getCaptchaCode
     * @return: org.springframework.web.servlet.ModelAndView
     * @Description: 数字验证码 返回byte数组形式, 前端请求类型arraybuffer
     */
    @GetMapping("/captcha/numCode")
    public String getCaptchaCode(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        // 生成验证码文本
        String captchaText = captchaProducer.createText();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, captchaText);
        session.setMaxInactiveInterval(5 * 60); // 5分钟过期
        log.info("生成验证码文本==== {}", captchaText);

        // 创建验证码图片
        BufferedImage bi = captchaProducer.createImage(captchaText);

        // 将验证码图片转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", outputStream);
        byte[] captchaBytes = outputStream.toByteArray();

        String base64Image = Base64.getEncoder().encodeToString(captchaBytes);

        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        arraybuffer形式
//        return ResponseEntity
//                .ok()
//                .headers(headers)
//                .body(captchaBytes);
        // 这个可以在前端通过response.headers.responsetype获取
        headers.set("responseType", "text"); // 设置自定义的responseType头部

//        return ResponseEntity
//                .ok()
//                .headers(headers)
//                .contentType(MediaType.TEXT_PLAIN)
//                .body(base64Image);
        return base64Image;

    }
}
