package com.z.framework.security.aop;

import cn.hutool.extra.spring.SpringUtil;
import com.z.framework.security.config.SpringSecurityAutoConfiguration;
import com.z.framework.security.service.TokenProviderService;
import com.z.framework.security.util.SecurityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    
    private final TokenProviderService tokenProviderService;


    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(TokenProviderService tokenProviderService, UserDetailsService userDetailsService) {
        this.tokenProviderService = tokenProviderService;
        this.userDetailsService = userDetailsService;
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

        // token认证核心
//        验证是否有认证信息
        final String s = extractHeaderToken(request);
        final String cookie = getCookie(request);
        if (StringUtils.isBlank(s) && StringUtils.isBlank(cookie)) {
            throw new RuntimeException("Token为空");
        }
        try {

            // 校验token是否已经退出, 或者被下线, 注: 这种方式下其实相当与token就变得有状态了, 坑
//            final CacheManager simpleCacheManager = SpringUtil.getBean(CacheManager.class);
//            final Cache tokenWriteListCache = simpleCacheManager.getCache("tokenWriteListCache");
//            if(Objects.isNull(tokenWriteListCache.get("tokenWriteList"))){
//                throw new RuntimeException("Token已过期");
//            }
//            List<String> tokenWriteList = (List<String>) tokenWriteListCache.get("tokenWriteList").get();
//            if(!tokenWriteList.contains(TokenProviderService.TOKEN_PREFIX + s) && !tokenWriteList.contains(TokenProviderService.TOKEN_PREFIX + cookie)){
//                throw new RuntimeException("Token已过期");
//            }

            // 这里可以直接返回AbstractAuthenticationToken,但是此时返回信息缺少权限信息
            AbstractAuthenticationToken authenticationToken = buildAuthentication(request, response);
            // 主动调用authenticate方法才可利用到loadUserByUsername方法，从而在authentication中填充权限信息
            // 或者在build过程中直接主动填充即可
//            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            //org.springframework.boot.web.servlet.filter.ErrorPageSecurityFilter.isAllowed
            // 如果没有下边设置，上述校验不通过, 就会403
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            logger.error("Token已过期:", e);
            removeTokenWriteList();
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
     * @data: 2023/6/1-上午11:22
     * @User: zhaozhiwei
     * @method: removeTokenWriteList

     * @return: void
     * @Description: 删除白名单中token
     */
    public void removeTokenWriteList() {
        // 退出时同时从白名单下线登录用户
        final CacheManager simpleCacheManager = SpringUtil.getBean(CacheManager.class);
        final Cache tokenBlackCache = simpleCacheManager.getCache("tokenWriteListCache");
        List<String> tokenWriteList;
        if(!Objects.isNull(tokenBlackCache.get("tokenWriteList"))){
            tokenWriteList = (List<String>) tokenBlackCache.get("tokenWriteList").get();
            tokenWriteList.remove(SecurityUtils.getTokenId());
            tokenBlackCache.put("tokenWriteList", tokenWriteList);
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
                final UserDetails userDetails = retrieveUser(userName);

                final UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());

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
                    extensionMap.put("year", LocalDateTime.now().getYear());

                    // 可以这里添加其它必要信息
                    authenticationToken.setDetails(extensionMap);
                }
                return authenticationToken;
            }
        }
        return null;
    }

    protected final UserDetails retrieveUser(String username) throws AuthenticationException {
        UserDetails loadedUser;
        try {
            // 调用loadUserByUsername时加入type前缀
            loadedUser = this.userDetailsService.loadUserByUsername(/*authentication.getType() + "&:@" +*/ username);
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
