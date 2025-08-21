package com.z.module.system.web.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.code.kaptcha.Constants;
import com.z.framework.security.service.TokenProviderService;
import com.z.module.system.domain.Upload;
import com.z.module.system.domain.User;
import com.z.module.system.domain.UserAuthority;
import com.z.module.system.repository.UploadRepository;
import com.z.module.system.repository.UserAuthorityRepository;
import com.z.module.system.repository.UserRepository;
import com.z.module.system.service.LoginService;
import com.z.module.system.service.consumer.EmailRegisterConsumer;
import com.z.module.system.service.consumer.SmsRegisterConsumer;
import com.z.module.system.web.vo.AuthedRespVO;
import com.z.module.system.web.vo.LoginVO;
import com.z.module.system.web.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.*;
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

    private final PasswordEncoder passwordEncoder;

    private final TokenProviderService tokenProviderService;

    private final LoginService loginService;

    private final UploadRepository uploadRepository;

    private final UserAuthorityRepository userAuthorityRepository;

    private final RocketMQTemplate rocketMQTemplate;

    private final AuthenticationManager authenticationManager;

    public LoginResource(UserRepository userRepository, TokenProviderService tokenProviderService, LoginService loginService, UploadRepository uploadRepository, UserAuthorityRepository userAuthorityRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, RocketMQTemplate rocketMQTemplate) {
        this.userRepository = userRepository;
        this.tokenProviderService = tokenProviderService;
        this.loginService = loginService;
        this.uploadRepository = uploadRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.rocketMQTemplate = rocketMQTemplate;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
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
        session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
        final AuthedRespVO authedRespVO = new AuthedRespVO();
        authedRespVO.setUsername(loginVM.getUsername());
        // 3. 不相同则返回登录页面
        if (!attribute.equals(captcha)) {
            logger.error("登录出错, 请输入正确的验证码");
            throw new RuntimeException("验证码错误");
        }

        try {

            log.info("登录用户信息 {}", loginVM);
            String username = loginVM.getUsername();
            String password = loginVM.getPassword();
            // security认证核心
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = tokenProviderService.generateToken(username, loginVM.isRememberMe());
            // 如果不需要前台动态控制按钮显示,可以返回***
            // authedRespVO.setPermissions(Collections.singletonList("*.*.*"));
            authedRespVO.setPermissions(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
            authedRespVO.setToken(token);

            // 获取用户头像
            final User dbUser = userRepository.findOneByLogin(username).orElse(new User());
            Long avatar = dbUser.getAvatar();
            if (Objects.isNull(avatar)) {
                avatar = 0L;
            }
            Optional<Upload> byCreatedBy = uploadRepository.findById(avatar);
            if (byCreatedBy.isPresent()) {
                String base64Image = Base64.getEncoder().encodeToString(byCreatedBy.get().getValue());
                authedRespVO.setAvatar(base64Image);
            }
            return authedRespVO;
        } catch (Exception e) {
            logger.error("登录出错", e);
            throw new RuntimeException("登录出错" + e.getMessage());
        }
    }


    /**
     * @Description:
     *
    http.logout(logout -> logout
            .logoutUrl("/api/system/loginOut")  // 必须与前端调用路径一致
    .invalidateHttpSession(true)       // 兼容有状态场景
    security配置了logout被匹配以后，就不会走这里的loginOut了
     * @author: zhaozhiwei
     * @data: 2025/4/12-18:41

     * @return: java.lang.String
    */

    @Operation(description = "退出")
    @PostMapping("loginOut")
    public String loginOut() {

        // 删除当前token
//        loginService.removeTokenWriteList();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LogoutSuccessEvent event = new LogoutSuccessEvent(authentication);
        SpringUtil.getApplicationContext().publishEvent(event);

        return "success";
    }

    @Operation(description = "新增用户")
    @PostMapping("/register")
    @Transactional(rollbackFor = Exception.class)
    public User register(@RequestBody UserVO userVO) throws URISyntaxException {
        log.debug("REST request to save User : {}", userVO);

        if (Objects.isNull(userVO.getPassword())) {
            userVO.setPassword("1");
        }
        userVO.setName(userVO.getLogin());

        // 密码加密
        String encryptedPassword = passwordEncoder.encode(userVO.getPassword());
        userVO.setPassword(encryptedPassword);

        if (Objects.isNull(userVO.getId())) {
            userVO.setActivated(true);
        }

        final User user = new User();
        BeanUtil.copyProperties(userVO, user);

        User newUser = userRepository.save(user);

        // 保存用户角色信息, 4:用户
        final String roleIdListStr = "4";
        final List<Long> roleIdList = Arrays.stream(roleIdListStr.split(",")).map(Long::valueOf).toList();
        final List<UserAuthority> userAuthorities = roleIdList.stream().map(roleId -> {
            final UserAuthority userAuthority = new UserAuthority();
            userAuthority.setRoleId(roleId);
            userAuthority.setUserId(newUser.getId());
            return userAuthority;
        }).collect(Collectors.toList());
        userAuthorityRepository.saveAll(userAuthorities);

        // 发送邮件或者短信通知用户注册成功
        rocketMQTemplate.send(EmailRegisterConsumer.TOPIC, MessageBuilder.withPayload(userVO).build());
        rocketMQTemplate.send(SmsRegisterConsumer.TOPIC, MessageBuilder.withPayload(userVO).build());

        return newUser;
    }
}
