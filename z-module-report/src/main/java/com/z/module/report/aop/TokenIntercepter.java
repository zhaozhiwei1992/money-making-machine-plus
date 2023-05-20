package com.z.module.report.aop;

import com.z.framework.security.service.TokenProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: TokenIntercepter
 * @Package com/z/module/report/aop/TokenIntercepter.java
 * @Description: 拦截器可以直接注入spring ioc对象
 * @date 2023/5/20 下午11:55
 */
@Component
@Slf4j
public class TokenIntercepter extends HandlerInterceptorAdapter {

    private final TokenProviderService tokenProviderService;

    public TokenIntercepter(TokenProviderService tokenProviderService) {
        this.tokenProviderService = tokenProviderService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String token = request.getParameter("token");
        if (StringUtils.hasText(tokenProviderService.validateToken(token))) {
            log.info("验证通过, token: {}", token);
            return super.preHandle(request, response, handler);
        }
        return false;
    }
}
