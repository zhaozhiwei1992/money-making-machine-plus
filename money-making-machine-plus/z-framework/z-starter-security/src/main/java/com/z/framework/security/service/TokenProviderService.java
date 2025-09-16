package com.z.framework.security.service;

import com.z.framework.security.config.CustomSecurityProperties;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TokenProviderService {

    /**
     * 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 表头授权
     */
    public static final String AUTHORIZATION = "Authorization";

    private final Key key;

    private final JwtParser jwtParser;

    private final int tokenValidityInMilliseconds;

    private final int tokenValidityInMillisecondsForRememberMe;

    public TokenProviderService(CustomSecurityProperties customSecurityProperties) {
        byte[] keyBytes;
        String secret = customSecurityProperties.getAuthentication().getJwt().getBase64Secret();
        if (!ObjectUtils.isEmpty(secret)) {
            log.debug("Using a Base64-encoded JWT secret key");
            keyBytes = Decoders.BASE64.decode(secret);
        } else {
            log.warn(
                    "Warning: the JWT key used is not Base64-encoded. " +
                            "We recommend using the `z.security.authentication.jwt.base64-secret` key for " +
                            "optimum security."
            );
            secret = customSecurityProperties.getAuthentication().getJwt().getSecret();
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        key = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parser().setSigningKey(key);
        this.tokenValidityInMilliseconds =
                customSecurityProperties.getAuthentication().getJwt().getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe =
                customSecurityProperties.getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe();
    }

    /**
     * @data: 2023/5/20-下午6:40
     * @User: zhaozhiwei
     * @method: createToken
      * @param userName :
 * @param rememberMe :
     * @return: java.lang.String
     * @Description: 生成token
     */
    public String generateToken(String userName, boolean rememberMe) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        // 设置签发时间
        calendar.setTime(new Date());
        // 设置过期时间
        // 添加秒钟
        if (rememberMe) {
            calendar.add(Calendar.SECOND, this.tokenValidityInMillisecondsForRememberMe);
        } else {
            calendar.add(Calendar.SECOND, this.tokenValidityInMilliseconds);
        }
        Date time = calendar.getTime();
        HashMap<String, Object> map = new HashMap<>();
        //you can put any data in the map
        map.put("userName", userName);

        String jwt = Jwts.builder()
                .setClaims(map)
                //签发时间
                .setIssuedAt(now)
                //过期时间
                .setExpiration(time)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        //jwt前面一般都会加Bearer
        return TOKEN_PREFIX + jwt;
    }

    /**
     * @data: 2023/5/20-下午9:56
     * @User: zhaozhiwei
     * @method: validateToken
      * @param authToken :
     * @return: java.lang.String
     * @Description: 校验没有异常返回用户名
     */
    public String validateToken(String authToken) {

        if(!StringUtils.hasText(authToken)){
            throw new RuntimeException("认证失败");
        }

        Map<String, Object> body =
                // 去掉前缀解析, 或者token传入前就处理掉
                jwtParser.parseClaimsJws(authToken.replace(TOKEN_PREFIX, ""))
                        .getBody();
        return body.get("userName").toString();
    }
}
