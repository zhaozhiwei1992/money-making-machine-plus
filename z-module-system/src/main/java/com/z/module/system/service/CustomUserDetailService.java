package com.z.module.system.service;

import cn.hutool.core.util.StrUtil;
import com.z.module.system.domain.Menu;
import com.z.module.system.domain.RoleMenu;
import com.z.module.system.domain.User;
import com.z.module.system.domain.UserAuthority;
import com.z.module.system.repository.MenuRepository;
import com.z.module.system.repository.RoleMenuRepository;
import com.z.module.system.repository.UserAuthorityRepository;
import com.z.module.system.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("userDetailService")
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    private final MenuRepository menuRepository;

    private final UserAuthorityRepository userAuthorityRepository;

    private final RoleMenuRepository roleMenuRepository;

    public CustomUserDetailService(UserRepository userRepository, MenuRepository menuRepository,
                                   UserAuthorityRepository userAuthorityRepository,
                                   RoleMenuRepository roleMenuRepository) {
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.roleMenuRepository = roleMenuRepository;
    }

    private static final String LOAD_BY_USERNAME_CACHE = "loadByUserNameCache";

    @Override
    @Cacheable(cacheNames = LOAD_BY_USERNAME_CACHE)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findOneByLogin(username).orElse(new User());
        if (StrUtil.isEmpty(user.getLogin())) {
            log.error("找不到该用户，用户名：{}", username);
            throw new UsernameNotFoundException("用户:" + username + ",不存在!");
        }

        Set<String> permissionList;
        if ("admin".equals(username)) {
            permissionList =
                    menuRepository.findAll().stream().map(Menu::getPermissionCode).collect(Collectors.toSet());
        } else {
            // 获取角色信息
            final List<UserAuthority> userAuthorities = userAuthorityRepository.findAllByUserId(user.getId());
            // 获取角色对菜单信息
            final List<RoleMenu> roleMenuList =
                    roleMenuRepository.findByRoleIdIn(userAuthorities.stream().map(UserAuthority::getRoleId).collect(Collectors.toList()));
            // 获取权限信息
            final List<Menu> allMenuList = menuRepository.findAllById(roleMenuList.stream().map(RoleMenu::getMenuId).collect(Collectors.toList()));
            permissionList = allMenuList.stream().map(Menu::getPermissionCode).collect(Collectors.toSet());
        }
        // 设置功能权限信息,方便后续校验 system:user:add之类的
        List<GrantedAuthority> grantedAuthorities = permissionList.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getLogin(),
                user.getPassword(),
                grantedAuthorities);
    }
}
