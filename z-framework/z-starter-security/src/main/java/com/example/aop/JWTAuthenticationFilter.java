package com.example.aop;

import com.alibaba.fastjson.JSON;
import com.example.config.SpringSecurityConfig;
import com.example.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String url = request.getRequestURI();

        //精确匹配跳过
        if (Arrays.asList(SpringSecurityConfig.AUTH_WHITELIST).contains(url)) {
            chain.doFilter(request, response);
            return;
        }

        // 满足表达式的跳过
        final String[] writeUrlList = SpringSecurityConfig.AUTH_WHITELIST;
        for (String writeUrl : writeUrlList) {
            if (antPathMatcher.match(writeUrl, url)) {
                chain.doFilter(request, response);
                return;
            }
        }

        Map map = new HashMap();
//        验证是否有认证信息
        final String s = extractHeaderToken(request);
        final String cookie = getCookie(request);
        if (StringUtils.isBlank(s) && StringUtils.isBlank(cookie)) {
            map.put("codeCheck", false);
            map.put("msg", "Token为空");
            response.setCharacterEncoding("UTF-8");
            if(url.startsWith(IfmisProperties.RESOURCE_PRE)){
                response.getWriter().write(JSON.toJSONString(map));
            }else{
                // 界面请求未登录重定向到登录界面
                response.sendRedirect("/login");
            }
            return;
        }
        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request, response);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            map.put("codeCheck", false);
            map.put("msg", "Token已过期");
            response.setCharacterEncoding("UTF-8");
            logger.error("Token已过期:", e);
        } catch (UnsupportedJwtException e) {
            //json.put("status", "-3");
            map.put("codeCheck", false);
            map.put("msg", "Token格式错误");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(map));
            logger.error("Token格式错误: ", e);
        } catch (MalformedJwtException e) {
            map.put("codeCheck", false);
            map.put("msg", "Token没有被正确构造");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(map));
            logger.error("Token没有被正确构造:", e);
        } catch (IllegalArgumentException e) {
            map.put("codeCheck", false);
            map.put("msg", "Token非法参数异常");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(map));
            logger.error("非法参数异常:", e);
        } catch (Exception e) {
            map.put("codeCheck", false);
            map.put("msg", "Invalid Token");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSON.toJSONString(map));
            logger.error("Invalid Token ", e);
        }
    }

    /**
     * Extract the OAuth bearer token from a header.
     *
     * @param request The request.
     * @return The token, or null if no OAuth authorization header was supplied.
     */
    protected String extractHeaderToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(JwtUtil.AUTHORIZATION);
        // typically there is only one (most servers enforce that)
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if ((value.toLowerCase().startsWith(JwtUtil.TOKEN_PREFIX.toLowerCase()))) {
                String authHeaderValue = value.substring(JwtUtil.TOKEN_PREFIX.length()).trim();
                if ("null".equals(authHeaderValue)) {
                    return null;
                }
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }

        return null;
    }

    public final static String TOKEN = "token";

    protected String getCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, JwtUtil.AUTHORIZATION);
        if (cookie == null) {
            return null;
        }
        String value = cookie.getValue();
        if (!org.springframework.util.StringUtils.hasLength(value)) {
            return null;
        }
        return value;
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,
                                                                  HttpServletResponse response) {
        String token = extractHeaderToken(request);

        if (token == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Token not found in headers. Trying request parameters."
                        + request.getRequestURI() + "?" + request.getQueryString());
            }
            token = getCookie(request);
        }

        if (token != null) {
            String userName;

            // 解密Token
            userName = JwtUtil.validateToken(token);
            if (StringUtils.isNotBlank(userName)) {

                // 获取数据库用户, 进行数据库用户二次校验, 根据用户状态控制是否允许登录
                // retrieveUser(userName);

                final UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userName, null, new ArrayList<>());

                // 通过security一个请求线程中带一些特殊信息, 通过request带也可以
                final Object details = authenticationToken.getDetails();
                if (Objects.isNull(details)) {
                    final Map extensionMap = new HashMap();
                    WebAuthenticationDetails webAuthenticationDetails =
                            new WebAuthenticationDetailsSource().buildDetails(request);
                    if (webAuthenticationDetails.getRemoteAddress() != null) {
                        extensionMap.put("remoteAddress", webAuthenticationDetails.getRemoteAddress());
                    }
                    if (webAuthenticationDetails.getSessionId() != null) {
                        extensionMap.put("sessionId", webAuthenticationDetails.getSessionId());
                    }

                    extensionMap.put("tokenid", token);

                    // 根据请求参数解析出区划和年度信息
                    final Map<String, String> map = extractRequest(request, userName);
                    extensionMap.put("province", map.get("province"));
                    extensionMap.put("year", map.get("year"));
                    authenticationToken.setDetails(extensionMap);
                }

                return authenticationToken;

            }
            return null;
        }
        return null;
    }

    /**
     * @Description: 区划+年度表达式
     */
    private static final Pattern PROVINCE_YEAR = Pattern.compile("\\d{9}\\/\\d{4}\\/");

    /**
     * @param request  :
     * @param userName
     * @data: 2022/12/6-下午4:36
     * @User: zhaozhiwei
     * @method: extractRequest
     * @return: java.util.Map<java.lang.String, java.lang.String>
     * @Description: 根据请求解析出区划, 年度等信息
     */
    private Map<String, String> extractRequest(HttpServletRequest request, String userName){
        final String url = request.getRequestURL().toString();
        final String method = request.getMethod();
        final Map<String, String> map = new HashMap<>();
        final Matcher m = PROVINCE_YEAR.matcher(url);
        while (m.find()) {
            final String match = m.group(0);
            final String[] split = match.split("\\/");
            //1. 解析区划
            map.put("province", split[0]);
            //2. 解析年度
            map.put("year", split[1]);
            break;
        }
        if(StringUtils.isEmpty(map.get("year"))){
            map.put("year", String.valueOf(LocalDateTime.now().getYear()));
        }
        return map;
    }

    private final UserDetailsService userDetailsService;

    /**
     * @Title: JWTAuthenticationFilter
     * @Package com/longtu/aop/JWTAuthenticationFilter.java
     * @Description: 可在用户锁定或其它状态是控制token不允许登录
     * @author zhaozhiwei
     * @date 2022/12/6 下午4:23
     * @version V1.0
     */
    protected final UserDetails retrieveUser(String username) throws AuthenticationException {
        UserDetails loadedUser;
        try {
            // 调用loadUserByUsername时加入type前缀
            loadedUser = this.userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException var6) {
            throw var6;
        } catch (Exception var7) {
            throw new InternalAuthenticationServiceException(var7.getMessage(), var7);
        }

        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException("用户[" + username + "]不存在！");
        } else {
            return loadedUser;
        }
    }

}
