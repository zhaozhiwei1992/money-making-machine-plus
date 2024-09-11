package com.z.module.system.aop;

import com.z.framework.security.aop.AbstractPermissionFilterTemplate;
import com.z.module.system.domain.*;
import com.z.module.system.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.z.framework.security.config.SpringSecurityAutoConfiguration.AUTH_WHITELIST;

/**
 * @Title: CustomPermissionFilter
 * @Package com/z/module/system/aop/CustomPermissionFilter.java
 * @Description: 动态权限控制方案，比如按接口控制
 * @author zhaozhiwei
 * @date 2024/8/13 上午11:36
 * @version V1.0
 */
//@Component
public class CustomPermissionFilter extends AbstractPermissionFilterTemplate {

    private final UserAuthorityRepository userAuthorityRepository;

    private final UserRepository userRepository;

    private final MenuRepository menuRepository;

    private final RoleMenuRepository roleMenuRepository;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public CustomPermissionFilter(UserAuthorityRepository userAuthorityRepository,
                                  UserRepository userRepository, MenuRepository menuRepository, RoleMenuRepository roleMenuRepository) {
        this.userAuthorityRepository = userAuthorityRepository;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
        this.roleMenuRepository = roleMenuRepository;
    }


    public static String[] PERMISSION_WHITELIST = {
            "/api/menus/route",
            "/api/dict/list"
    };

    public boolean hasPermission(Authentication authentication, HttpServletRequest request) {

        // 实现具体的权限检查逻辑
        // 注意, 白名单不能被拦截
        Set<String> urls = new HashSet<>(Arrays.asList(AUTH_WHITELIST));

        final String requestURI = request.getRequestURI();
        for (String url : urls) {
            if (antPathMatcher.match(url, requestURI)) {
                return true;
            }
        }

        Set<String> permissionsWhiteList = new HashSet<>(Arrays.asList(PERMISSION_WHITELIST));
        for (String s : permissionsWhiteList) {
            if (antPathMatcher.match(s, requestURI)) {
                return true;
            }
        }


        Object principal = authentication.getPrincipal();
        // 管理员用户全部放行
        if ("admin".equals(principal)) {
            return true;
        }

        // 2. 根据用户名去加载数据库中所属角色对应的所有权限标识
        final Optional<User> oneByLogin = userRepository.findOneByLogin(principal.toString());
        if (!oneByLogin.isPresent()) {
            return false;
        }
        final List<UserAuthority> roleList = userAuthorityRepository.findAllByUserId(oneByLogin.get().getId());

        //TODO 前后端分离项目,前端请求rest接口应传递权限标识符,从而校验权限
        // 使用统一的过滤器，避免每个方法增加权限preAuthority注解,麻烦
        //TODO 角色权限表,获取接口权限

        return false;
    }
}