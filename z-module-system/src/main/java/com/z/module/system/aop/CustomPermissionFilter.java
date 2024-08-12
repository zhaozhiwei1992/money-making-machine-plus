package com.z.module.system.aop;

import com.z.framework.security.aop.AbstractPermissionFilterTemplate;
import com.z.module.system.domain.*;
import com.z.module.system.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.z.framework.security.config.SpringSecurityAutoConfiguration.AUTH_WHITELIST;

@Component
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

        //TODO 根据url或者前端传递permissionCode过来
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

        // 1. 获取当前请求菜单权限标识, 如: system:user:add
        final Optional<Menu> oneByUrl = menuRepository.findOneByUrl(requestURI);
        if (!oneByUrl.isPresent()) {
            return false;
        }

        // 2. 根据用户名去加载数据库中所属角色对应的所有权限标识
        final Optional<User> oneByLogin = userRepository.findOneByLogin(principal.toString());
        if (!oneByLogin.isPresent()) {
            return false;
        }
        final List<UserAuthority> roleList = userAuthorityRepository.findAllByUserId(oneByLogin.get().getId());

        // 角色菜单/按钮权限
        final List<Long> roleIdList = roleList.stream().map(UserAuthority::getRoleId).collect(Collectors.toList());
        final List<RoleMenu> roleMenuList = roleMenuRepository.findByRoleIdIn(roleIdList);
        if (roleMenuList.stream().map(RoleMenu::getMenuId).collect(Collectors.toList()).contains(oneByUrl.get().getId())) {
            return true;
        }

        return false;
    }
}