package com.z.framework.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title: JwtUtil
 * @Package com/example/springbootsecurityjwt/util/JwtUtil.java
 * @Description: token生成
 * 工具类方式写的太死, 建议使用 {@see TokenProviderService}
 * @author zhaozhiwei
 * @date 2021/1/4 下午5:29
 * @version V1.0
 */
public class JwtUtil {
    /**过期时间---24 hour*/
    private static final int EXPIRATION_TIME = 60*60*24;
    /**自己设定的秘钥*/
    private static final String SECRET = "023bdc63c3c5a4587*9ee6581508b9d03ad39a74fc0c9a9cce604743367c9646b";
    private static final Key key;
    /**前缀*/
    public static final String TOKEN_PREFIX = "Bearer ";
    /**表头授权*/
    public static final String AUTHORIZATION = "Authorization";

    static {
        // 方式1:
        byte[] keyBytes;
        String secret = "XX#$%()(#*!()!KL<><MQLMNQNQJQK sdfkjsdrow32234545fdf>?N<:{LWPW";
        if (!StringUtils.hasText(secret)) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        } else {
//            keyBytes = Decoders.BASE64.decode("f3973b64918e4324ad85acea1b6cbec5");
            //Caused by: io.jsonwebtoken.security.WeakKeyException: The specified key byte array is 192 bits which is not secure enough for any JWT HMAC-SHA algorithm.  The JWT JWA Specification (RFC 7518, Section 3.2) states that keys used with HMAC-SHA algorithms MUST have a size >= 256 bits (the key size must be greater than or equal to the hash output size).  Consider using the io.jsonwebtoken.security.Keys#secretKeyFor(SignatureAlgorithm) method to create a key guaranteed to be secure enough for your preferred HMAC-SHA algorithm.  See https://tools.ietf.org/html/rfc7518#section-3.2 for more information.
            keyBytes = Decoders.BASE64.decode("f3973b64918e4324ad85acea1b6cbec5f3973b64918e4324ad85acea1b6cbec5");
        }
        key = Keys.hmacShaKeyFor(keyBytes);
//        方式2: 使用JWT库生成安全密钥（最佳实践)
//        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }


    /**
     *
     * @author: xxm
     * 功能描述:创建Token, 生成方式和解析方式一定是配套
     * @date: 2020/5/28 16:09
     * @param: 
     * @return: 
     */
    public static String generateToken(String userName) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        // 设置签发时间
        calendar.setTime(new Date());
        // 设置过期时间
        // 添加秒钟
        calendar.add(Calendar.SECOND, EXPIRATION_TIME);
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
     *
     * @author: xxm
     * 功能描述: 解密Token
     * @date: 2020/5/28 16:18
     * @param: 
     * @return: 
     */
    public static String validateToken(String token) {

        // parse the token.
        Map<String, Object> body = Jwts.parser()
                .setSigningKey(key)
                // 去掉前缀解析, 或者token传入前就处理掉
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody();
        return body.get("userName").toString();
    }

    public static void main(String[] args) {
        final String admin = generateToken("admin");
        System.out.println(admin);

        //JWT expired at 2021-01-05T09:10:09Z. Current time: 2021-06-02T02:08:12Z, a difference of 12761883519 milliseconds.
        final String s = validateToken("Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyTmFtZSI6ImFkbWluIiwiaWF0IjoxNzY2MTI3MDE2LCJleHAiOjE3NjYyMTM0MTZ9.Hw5psvvWs8LBEKUW_EMIeojcG12tQc5DwVy5xr9EnIU");
        System.out.println(s);
    }
}
