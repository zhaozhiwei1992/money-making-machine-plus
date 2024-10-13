package com.z.module.system.web.rest;

import com.z.framework.security.service.TokenProviderService;
import com.z.framework.security.util.SecurityUtils;
import com.z.module.system.domain.User;
import com.z.module.system.repository.UserRepository;
import com.z.module.system.service.LoginLogService;
import com.z.module.system.web.vo.AuthedRespVO;
import com.z.module.system.web.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

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
@RequestMapping(value = {"/system/mobile"})
@Slf4j
public class MobileLoginResource {

    private static final Logger logger = LoggerFactory.getLogger(MobileLoginResource.class);

    private final UserRepository userRepository;

    private final PasswordEncoder bCryptPasswordEncoder;

    private CacheManager cacheManager;

    private final LoginLogService loginLogService;

    private final TokenProviderService tokenProviderService;

    public MobileLoginResource(UserRepository userRepository, PasswordEncoder passwordEncoder, CacheManager cacheManager, LoginLogService loginLogService, TokenProviderService tokenProviderService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = passwordEncoder;
        this.cacheManager = cacheManager;
        this.loginLogService = loginLogService;
        this.tokenProviderService = tokenProviderService;
    }

    /**
     * @data: 2022/7/14-上午11:32
     * @User: zhaozhiwei
     * @method: login
     * @return: com.longtu.web.vm.ResponseData<java.lang.String>
     * @Description: 描述
     */
    @Operation(description = "用户密码登录认证")
    @PostMapping("/login")
    public AuthedRespVO loginUserNamePassword(@Valid @RequestBody LoginVO loginVM, HttpServletRequest request) {

        final AuthedRespVO authedRespVO = new AuthedRespVO();
        authedRespVO.setUsername(loginVM.getUsername());

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
        // 销毁token, 防止下次使用
        final Cache tokenBlackCache = cacheManager.getCache("tokenBlackCache");
        List<String> cacheBlockList;
        if(Objects.isNull(tokenBlackCache.get("tokenBlock"))){
            cacheBlockList = new ArrayList<>();
        }else{
            cacheBlockList = (List<String>) tokenBlackCache.get("tokenBlock").get();
        }
        cacheBlockList.add(SecurityUtils.getTokenId());
        tokenBlackCache.put("tokenBlack", cacheBlockList);

        return "success";
    }

    @Operation(description = "验证码登录认证")
    @PostMapping("/login/sms")
    public AuthedRespVO loginUserSms(@Valid @RequestBody LoginVO loginVM, HttpServletRequest request) {

        final AuthedRespVO authedRespVO = new AuthedRespVO();
        authedRespVO.setUsername(loginVM.getUsername());

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
                authedRespVO.setPermissions(Arrays.asList("*.*.*"));
                authedRespVO.setToken(token);

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
}
