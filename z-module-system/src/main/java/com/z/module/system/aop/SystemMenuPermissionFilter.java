package com.z.module.system.aop;

import com.z.framework.security.aop.MenuPermissionFilterTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.z.framework.security.config.SpringSecurityAutoConfiguration.AUTH_WHITELIST;

@Component
public class SystemMenuPermissionFilter extends MenuPermissionFilterTemplate {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    public boolean hasPermission(Authentication authentication, HttpServletRequest request) {
        // 实现具体的权限检查逻辑
        // 注意, 白名单不能被拦截
        Set<String> urls = new HashSet<>(Arrays.asList(AUTH_WHITELIST));

        for (String url : urls) {
            if (antPathMatcher.match(url, request.getRequestURI())) {
                return true;
            }
        }

        Object principal = authentication.getPrincipal();
        // 1. 获取当前请求菜单权限标识, 如: system:user:add
        // 2. 根据用户名去加载数据库中所属角色对应的所有权限标识
        // 3. 判断当前请求菜单权限标识是否在用户所拥有的权限标识中
        if("admin".equals(principal)){
            return true;
        }

        return false;
    }
}