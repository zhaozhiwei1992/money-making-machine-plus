package com.z.framework.security.aop;

import cn.hutool.extra.spring.SpringUtil;
import com.z.framework.security.config.SpringSecurityAutoConfiguration;
import com.z.framework.security.service.TokenProviderService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    
    private TokenProviderService tokenProviderService;

    public JWTAuthenticationFilter(TokenProviderService tokenProviderService) {
        this.tokenProviderService = tokenProviderService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String url = request.getRequestURI();

        //精确匹配跳过
        if (Arrays.asList(SpringSecurityAutoConfiguration.AUTH_WHITELIST).contains(url)) {
            chain.doFilter(request, response);
            return;
        }

        // 满足表达式的跳过
        final String[] writeUrlList = SpringSecurityAutoConfiguration.AUTH_WHITELIST;
        for (String writeUrl : writeUrlList) {
            if (antPathMatcher.match(writeUrl, url)) {
                chain.doFilter(request, response);
                return;
            }
        }

//        验证是否有认证信息
        final String s = extractHeaderToken(request);
        final String cookie = getCookie(request);
        if (StringUtils.isBlank(s) && StringUtils.isBlank(cookie)) {
            throw new RuntimeException("Token为空");
        }
        try {

            // 校验token是否已经退出, 或者被下线
            final CacheManager simpleCacheManager = SpringUtil.getBean(CacheManager.class);
            final Cache tokenBlackCache = simpleCacheManager.getCache("tokenBlackCache");
            final Object tokenBlock = tokenBlackCache.get("tokenBlock");
            if(!Objects.isNull(tokenBlock)){
                List<String> cacheBlockList = (List<String>) tokenBlackCache.get("tokenBlock").get();
                if(cacheBlockList.contains(s) || cacheBlockList.contains(cookie)){
                    throw new RuntimeException("token已失效");
                }
            }

            // token认证核心
            UsernamePasswordAuthenticationToken authentication = buildAuthentication(request, response);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            logger.error("Token已过期:", e);
            throw new RuntimeException("Token已过期");
        } catch (UnsupportedJwtException e) {
            logger.error("Token格式错误: ", e);
            throw new RuntimeException("Token格式错误");
        } catch (MalformedJwtException e) {
            logger.error("Token没有被正确构造:", e);
            throw new RuntimeException("Token没有被正确构造");
        } catch (IllegalArgumentException e) {
            logger.error("非法参数异常:", e);
            throw new RuntimeException("Token非法参数异常");
        } catch (Exception e) {
            logger.error("Invalid Token ", e);
            throw new RuntimeException("Invalid Token");
        }
    }

    /**
     * Extract the OAuth bearer token from a header.
     *
     * @param request The request.
     * @return The token, or null if no OAuth authorization header was supplied.
     */
    protected String extractHeaderToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(TokenProviderService.AUTHORIZATION);
        // typically there is only one (most servers enforce that)
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if ((value.toLowerCase().startsWith(TokenProviderService.TOKEN_PREFIX.toLowerCase()))) {
                String authHeaderValue = value.substring(TokenProviderService.TOKEN_PREFIX.length()).trim();
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
        Cookie cookie = WebUtils.getCookie(request, TokenProviderService.AUTHORIZATION);
        if (cookie == null) {
            return null;
        }
        String value = cookie.getValue();
        if (!org.springframework.util.StringUtils.hasLength(value)) {
            return null;
        }
        return value;
    }

    /**
     * @param request  :
     * @param response :
     * @data: 2023/4/17-下午7:38
     * @User: zhaozhiwei
     * @method: buildAuthentication
     * @return: org.springframework.security.authentication.UsernamePasswordAuthenticationToken
     * @Description: 根据请求参数获取token构建authentication, 线程中使用
     */
    private UsernamePasswordAuthenticationToken buildAuthentication(HttpServletRequest request,
                                                                    HttpServletResponse response) {
        String token = extractHeaderToken(request);

        if (token == null) {
            logger.debug("Token not found in headers. Trying request parameters {} ? {}"
                    , request.getRequestURI(), request.getQueryString());
            token = getCookie(request);
        }

        if (token != null) {
            String userName;

            // 解密Token
            userName = tokenProviderService.validateToken(token);
            if (StringUtils.isNotBlank(userName)) {

                // 获取数据库用户, 进行数据库用户二次校验, 根据用户状态控制是否允许登录
                // retrieveUser(userName);

                final UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userName, null, new ArrayList<>());

                // 通过security一个请求线程中带一些特殊信息, 通过request带也可以
                final Object details = authenticationToken.getDetails();
                if (Objects.isNull(details)) {
                    final Map<String, Object> extensionMap = new HashMap<>();
                    WebAuthenticationDetails webAuthenticationDetails =
                            new WebAuthenticationDetailsSource().buildDetails(request);
                    if (webAuthenticationDetails.getRemoteAddress() != null) {
                        extensionMap.put("remoteAddress", webAuthenticationDetails.getRemoteAddress());
                    }
                    if (webAuthenticationDetails.getSessionId() != null) {
                        extensionMap.put("sessionId", webAuthenticationDetails.getSessionId());
                    }

                    extensionMap.put("tokenid", token);
                    // 可以这里添加其它必要信息
                    authenticationToken.setDetails(extensionMap);
                }
                return authenticationToken;
            }
        }
        return null;
    }
}
