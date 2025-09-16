package com.z.framework.security.aop;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Title: MenuPermissionFilter
 * @Package com/z/framework/security/aop/MenuPermissionFilter.java
 * @Description: 功能权限设计模板方法
 * @author zhaozhiwei
 * @date 2024/7/20 上午2:35
 * @version V1.0
 */
public abstract class AbstractPermissionFilterTemplate extends OncePerRequestFilter {

    public boolean hasPermission(Authentication authentication, HttpServletRequest request) {
        return false;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 检查JWT认证是否通过
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // 检查用户是否有权访问该资源
            if (!hasPermission(authentication, request)) {
                // 如果没有权限，返回403响应
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return;
            }
        }
        // 如果有权限或无需认证，继续过滤器链
        filterChain.doFilter(request, response);
    }
}