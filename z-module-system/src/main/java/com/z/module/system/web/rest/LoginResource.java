package com.z.module.system.web.rest;

import com.google.code.kaptcha.Constants;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.framework.security.util.JwtUtil;
import com.z.module.system.domain.User;
import com.z.module.system.repository.UserRepository;
import com.z.module.system.web.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: LoginResource
 * @Package com/longtu/web/rest/LoginResource.java
 * @Description: 登录认证接口
 * @date 2022/7/14 上午11:10
 */
@Tag(name = "登录API")
@RestController
@RequestMapping(value = {"/api"})
@Slf4j
public class LoginResource {

    private static final Logger logger = LoggerFactory.getLogger(LoginResource.class);

    private final UserRepository userRepository;

    private final PasswordEncoder bCryptPasswordEncoder;

    public LoginResource(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = passwordEncoder;
    }

    /**
     * @data: 2022/7/14-上午11:32
     * @User: zhaozhiwei
     * @method: login
     * @return: com.longtu.web.vm.ResponseData<java.lang.String>
     * @Description: 描述
     */
    @Operation(description = "登录认证")
    @PostMapping("/login")
    public ResponseEntity<ResponseData<String>> login(@Valid @RequestBody LoginVO loginVM, HttpServletRequest request) {

        // 验证码校验
        // 1. 获取写入的验证码信息
        final String captcha = loginVM.getCaptcha();
        // 2. 获取session中验证码信息
        final HttpSession session = request.getSession();
        final Object attribute = session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        // 3. 不相同则返回登录页面
        if(!attribute.equals(captcha)){
            logger.error("登录出错, 请输入正确的验证码");
            return ResponseData.fail("验证码错误");
        }

        try {
            log.info("登录用户信息 {}", loginVM);
            String username = loginVM.getUsername();
            String password = loginVM.getPassword();
            final User dbUser = userRepository.findOneByLogin(username).orElse(new User());
            logger.info("查询用户信息 {}", dbUser);
            String dbPassWord = dbUser.getPassword();
            logger.info("数据库密码: {}", dbPassWord);
            if (bCryptPasswordEncoder.matches(password, dbPassWord)) {
                String token = JwtUtil.generateToken(username);
                return ResponseData.ok(token);
            }else{
                log.error(String.format("登录失败, 用户: %s, 密码: %s, 数据库密码: %s", username, password, dbPassWord));
                return ResponseData.fail(String.format("用户密码不匹配, 登录用户: %s, 密码: %s", username, password));
            }
        } catch (Exception e) {
            logger.error("登录出错", e);
            return ResponseData.fail();
        }
    }
}