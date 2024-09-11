package com.z.module.screen.web.rest;

import com.z.framework.security.service.TokenProviderService;
import com.z.framework.security.util.SecurityUtils;
import com.z.module.screen.web.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * @Title: null.java
 * @Package: com.z.module.screen.web.rest
 * @Description: 适配goView登录接口
 * @author: zhaozhiwei
 * @date: 2023/5/30 下午4:20
 * @version: V1.0
 */
@RestController
@RequestMapping("/goview/sys")
@Slf4j
public class GoViewSysResource {

    private final PasswordEncoder bCryptPasswordEncoder;

    private final CacheManager cacheManager;

    private final TokenProviderService tokenProviderService;

    private final JdbcTemplate jdbcTemplate;

    public GoViewSysResource(PasswordEncoder bCryptPasswordEncoder, CacheManager cacheManager, TokenProviderService tokenProviderService, JdbcTemplate jdbcTemplate) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.cacheManager = cacheManager;
        this.tokenProviderService = tokenProviderService;
        this.jdbcTemplate = jdbcTemplate;
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
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginVO loginVM, HttpServletRequest request) {

        try {
            log.info("登录用户信息 {}", loginVM);
            String username = loginVM.getUsername();
            String password = loginVM.getPassword();
            final List<Map<String, Object>> dbUserMap = jdbcTemplate.queryForList("select * from t_user where login = ? ", Collections.singletonList(username).toArray());
            String dbPassWord = String.valueOf(dbUserMap.get(0).get("password_hash"));
            if (bCryptPasswordEncoder.matches(password, dbPassWord)) {
                String token = tokenProviderService.generateToken(username, loginVM.isRememberMe());
                Map<String, Object> map = new HashMap<>();
                final Map<String, Object> dbUser = dbUserMap.get(0);
                dbUser.put("nickname", dbUser.get("username"));
                dbUser.put("depId", 0);
                dbUser.put("postId", "0");
                map.put("userinfo", dbUser);
                final Map<String, Object> tokenInfo = new HashMap<>();
                tokenInfo.put("tokenName", "Authorization");
                tokenInfo.put("tokenValue", token);
                tokenInfo.put("isLogin", true);
//                        "loginId": "1",
//                        "loginType": "login",
//                        "tokenTimeout": 2592000,
//                        "sessionTimeout": 2592000,
//                        "tokenSessionTimeout": 2591893,
//                        "tokenActivityTimeout": -1,
//                        "loginDevice": "default-device",
//                        "tag": null
                map.put("token", tokenInfo);
                return ResponseEntity.ok(map);
            }else{
                log.error(String.format("登录失败, 用户: %s, 密码: %s, 数据库密码: %s", username, password, dbPassWord));
                throw new RuntimeException(String.format("用户密码不匹配, 登录用户: %s, 密码: %s", username, password));
            }
        } catch (Exception e) {
            log.error("登录出错", e);
            throw new RuntimeException("登录出错");
        }
    }

    @Operation(description = "退出")
    @GetMapping("logout")
    public ResponseEntity<String> loginOut(){
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

        return ResponseEntity.ok("x");
    }

    @Operation(description = "获取oss地址")
    @GetMapping("/getOssInfo")
    public ResponseEntity<String> getOssInfo() {
        return ResponseEntity.ok("x");
    }
}
