package com.example.web.rest;

import com.longtu.domain.User;
import com.longtu.repository.UserRepository;
import com.longtu.security.util.JwtUtil;
import com.longtu.web.rest.constants.ResponseCodeEnum;
import com.longtu.web.vm.ResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: LoginResource
 * @Package com/longtu/web/rest/LoginResource.java
 * @Description: 登录认证接口
 * @date 2022/7/14 上午11:10
 */
@Api(tags = "登录API")
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
     * @param username :
     * @param password :
     * @data: 2022/7/14-上午11:32
     * @User: zhaozhiwei
     * @method: login
     * @return: com.longtu.web.vm.ResponseData<java.lang.String>
     * @Description: 描述
     */
    @ApiOperation(value = "登录认证", notes = "返回token信息")
    @PostMapping("/login")
    public ResponseEntity<ResponseData<String>> login(String username, String password) {

        final ResponseData<String> responseData = new ResponseData<>();
        responseData.setCode(ResponseCodeEnum.FAILED.getCode());
        responseData.setMsg(ResponseCodeEnum.FAILED.getMsg());
        responseData.setData(null);

        try {
            logger.info("用户名: {}, 密码: {}", username, password);
            final User dbUser = userRepository.findOneByLogin(username).orElse(new User());
            logger.info("查询用户信息 {}", dbUser);
            String dbPassWord = dbUser.getPassword();
            logger.info("数据库密码: {}", dbPassWord);
            if (bCryptPasswordEncoder.matches(password, dbPassWord)) {
                String token = JwtUtil.generateToken(username);
                responseData.setData(token);
                responseData.setCode(ResponseCodeEnum.SUCCESS.getCode());
                responseData.setMsg(ResponseCodeEnum.SUCCESS.getMsg());
                return ResponseEntity.status(HttpStatus.OK).body(responseData);
            }else{
                responseData.setMsg(String.format("用户密码不匹配, 登录用户: %s, 密码: %s", username, password));
                log.error(String.format("登录失败, 用户: %s, 密码: %s, 数据库密码: %s", username, password, dbPassWord));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
            }
        } catch (Exception e) {
            logger.error("登录出错", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
        }
    }
}
