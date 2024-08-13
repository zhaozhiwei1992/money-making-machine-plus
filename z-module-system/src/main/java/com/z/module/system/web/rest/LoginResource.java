package com.z.module.system.web.rest;

import com.google.code.kaptcha.Constants;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.framework.security.service.TokenProviderService;
import com.z.module.system.domain.User;
import com.z.module.system.repository.UserRepository;
import com.z.module.system.service.LoginLogService;
import com.z.module.system.service.LoginService;
import com.z.module.system.web.vo.AuthedRespVO;
import com.z.module.system.web.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;

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
@Slf4j
public class LoginResource {

    private static final Logger logger = LoggerFactory.getLogger(LoginResource.class);

    private final UserRepository userRepository;

    private final PasswordEncoder bCryptPasswordEncoder;

    private final LoginLogService loginLogService;

    private final TokenProviderService tokenProviderService;

    private final LoginService loginService;

    public LoginResource(UserRepository userRepository, PasswordEncoder passwordEncoder, LoginLogService loginLogService, TokenProviderService tokenProviderService, LoginService loginService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = passwordEncoder;
        this.loginLogService = loginLogService;
        this.tokenProviderService = tokenProviderService;
        this.loginService = loginService;
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
    public ResponseEntity<ResponseData<AuthedRespVO>> login(@Valid @RequestBody LoginVO loginVM, HttpServletRequest request) {

        // 验证码校验
        // 1. 获取写入的验证码信息
        final String captcha = loginVM.getCaptcha();
        // 2. 获取session中验证码信息
        final HttpSession session = request.getSession();
        final Object attribute = session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        final AuthedRespVO authedRespVO = new AuthedRespVO();
        authedRespVO.setUsername(loginVM.getUsername());
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
                String token = tokenProviderService.generateToken(username, loginVM.isRememberMe());
                authedRespVO.setPermissions(Collections.singletonList("*.*.*"));
                authedRespVO.setToken(token);

                // 记录token白名单, 注: 如果cache使用 redis之类的, 可以跟token同步增加失效时间
                loginService.addTokenWriteList(token);

                // 登录成功后设置全局用户信息，方便后续使用, 前后端分离项目这一步其实也没啥用
                // com.z.framework.security.aop.JWTAuthenticationFilter.doFilterInternal
//                final UsernamePasswordAuthenticationToken authenticationToken =
//                        new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // 登录成功记录日志
                loginLogService.save(loginVM, request);
                return ResponseData.ok(authedRespVO);
            }else{
                log.error(String.format("登录失败, 用户: %s, 密码: %s, 数据库密码: %s", username, password, dbPassWord));
                return ResponseData.fail(String.format("用户密码不匹配, 登录用户: %s, 密码: %s", username, password));
            }
        } catch (Exception e) {
            logger.error("登录出错", e);
            return ResponseData.fail();
        }
    }

    @Operation(description = "退出")
    @GetMapping("loginOut")
    public ResponseEntity<ResponseData<Object>> loginOut(){

        // 删除当前token
        loginService.removeTokenWriteList();

        return ResponseData.ok();
    }
}
