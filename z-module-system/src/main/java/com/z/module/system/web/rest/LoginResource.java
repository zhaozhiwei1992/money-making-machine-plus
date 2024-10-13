package com.z.module.system.web.rest;

import com.google.code.kaptcha.Constants;
import com.z.framework.security.service.TokenProviderService;
import com.z.module.system.domain.Upload;
import com.z.module.system.domain.User;
import com.z.module.system.repository.UploadRepository;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Base64;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
@RequestMapping("/system")
@Slf4j
public class LoginResource {

    private static final Logger logger = LoggerFactory.getLogger(LoginResource.class);

    private final UserRepository userRepository;

    private final PasswordEncoder bCryptPasswordEncoder;

    private final LoginLogService loginLogService;

    private final TokenProviderService tokenProviderService;

    private final LoginService loginService;

    private final UserDetailsService userDetailsService;

    private final UploadRepository uploadRepository;

    public LoginResource(UserRepository userRepository, PasswordEncoder passwordEncoder, LoginLogService loginLogService, TokenProviderService tokenProviderService, LoginService loginService, UserDetailsService userDetailsService, UploadRepository uploadRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = passwordEncoder;
        this.loginLogService = loginLogService;
        this.tokenProviderService = tokenProviderService;
        this.loginService = loginService;
        this.userDetailsService = userDetailsService;
        this.uploadRepository = uploadRepository;
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
    public AuthedRespVO login(@Valid @RequestBody LoginVO loginVM, HttpServletRequest request) {

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
            throw new RuntimeException("验证码错误");
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
                // 如果不需要前台动态控制按钮显示,可以返回***
                // authedRespVO.setPermissions(Collections.singletonList("*.*.*"));
                // 根据按钮权限动态控制前端按钮展现
                final Collection<? extends GrantedAuthority> authorities = userDetailsService.loadUserByUsername(username).getAuthorities();
                authedRespVO.setPermissions(authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
                authedRespVO.setToken(token);

                // 获取用户头像
                Long avatar = dbUser.getAvatar();
                if(Objects.isNull(avatar)){
                    avatar = 0L;
                }
                Optional<Upload> byCreatedBy = uploadRepository.findById(avatar);
                if(byCreatedBy.isPresent()){
                    String base64Image = Base64.getEncoder().encodeToString(byCreatedBy.get().getValue());
                    authedRespVO.setAvatar(base64Image);
                }

                // 记录token白名单, 注: 如果cache使用 redis之类的, 可以跟token同步增加失效时间
                loginService.addTokenWriteList(token);

                // 登录成功记录日志
                loginLogService.save(loginVM, request);
                return authedRespVO;
            }else{
                log.error(String.format("登录失败, 用户: %s, 密码: %s, 数据库密码: %s", username, password, dbPassWord));
                throw new RuntimeException(String.format("用户密码不匹配, 登录用户: %s, 密码: %s", username, password));
            }
        } catch (Exception e) {
            logger.error("登录出错", e);
            throw new RuntimeException("登录出错");
        }
    }

    @Operation(description = "退出")
    @GetMapping("loginOut")
    public String loginOut(){

        // 删除当前token
        loginService.removeTokenWriteList();

        return "success";
    }
}
