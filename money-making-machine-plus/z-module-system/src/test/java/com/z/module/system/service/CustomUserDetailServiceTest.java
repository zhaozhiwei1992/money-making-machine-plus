package com.z.module.system.service;

import com.z.module.system.domain.*;
import com.z.module.system.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private UserAuthorityRepository userAuthorityRepository;

    @Mock
    private RoleMenuRepository roleMenuRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    private User adminUser;
    private User regularUser;
    private List<Menu> allMenus;
    private List<UserAuthority> userAuthorities;
    private List<RoleMenu> roleMenus;
    private List<Authority> roleList;

    @BeforeEach
    public void setUp() {
        // 创建测试用户数据
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setLogin("admin");
        adminUser.setPassword("adminPassword");
        adminUser.setName("Admin User");
        adminUser.setActivated(true);

        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setLogin("user");
        regularUser.setPassword("userPassword");
        regularUser.setName("Regular User");
        regularUser.setActivated(true);

        // 创建测试菜单数据
        Menu menu1 = new Menu();
        menu1.setId(1L);
        menu1.setName("用户管理");
        menu1.setPermissionCode("system:user:view");

        Menu menu2 = new Menu();
        menu2.setId(2L);
        menu2.setName("用户添加");
        menu2.setPermissionCode("system:user:add");

        Menu menu3 = new Menu();
        menu3.setId(3L);
        menu3.setName("角色管理");
        menu3.setPermissionCode("system:role:view");

        allMenus = Arrays.asList(menu1, menu2, menu3);

        // 创建用户权限（角色）数据
        UserAuthority userAuthority1 = new UserAuthority();
        userAuthority1.setId(1L);
        userAuthority1.setUserId(2L); // regularUser的ID
        userAuthority1.setRoleId(2L); // 普通用户角色ID

        userAuthorities = Arrays.asList(userAuthority1);

        // 创建角色菜单关系数据
        RoleMenu roleMenu1 = new RoleMenu();
        roleMenu1.setId(1L);
        roleMenu1.setRoleId(2L); // 普通用户角色ID
        roleMenu1.setMenuId(1L); // 用户管理菜单ID

        roleMenus = Arrays.asList(roleMenu1);

        Authority authority = new Authority();
        authority.setId(1L);
        authority.setCode("ROLE_ADMIN");
        roleList = Arrays.asList(authority);

        // 模拟repository行为

    }

    @Test
    public void testLoadUserByUsernameForAdmin() {
        when(userRepository.findOneByLogin("admin")).thenReturn(Optional.of(adminUser));
        when(menuRepository.findAll()).thenReturn(allMenus);
        // 测试管理员用户登录
        UserDetails userDetails = customUserDetailService.loadUserByUsername("admin");
        
        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("adminPassword", userDetails.getPassword());
        
        // 管理员应该拥有所有权限
        assertEquals(3, userDetails.getAuthorities().size());
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        assertTrue(authorities.contains("system:user:view"));
        assertTrue(authorities.contains("system:user:add"));
        assertTrue(authorities.contains("system:role:view"));
    }

    @Test
    public void testLoadUserByUsernameForRegularUser() {
        when(userRepository.findOneByLogin("user")).thenReturn(Optional.of(regularUser));
        when(menuRepository.findAllById(Collections.singletonList(1L))).thenReturn(Collections.singletonList(allMenus.get(0)));

        when(userAuthorityRepository.findAllByUserId(2L)).thenReturn(userAuthorities);

        when(roleMenuRepository.findByRoleIdIn(Collections.singletonList(2L))).thenReturn(roleMenus);
        // 测试普通用户登录
        UserDetails userDetails = customUserDetailService.loadUserByUsername("user");
//        when(authorityRepository.findAllById(userAuthorities.stream().map(UserAuthority::getRoleId).toList())).thenReturn(roleList);

        
        assertNotNull(userDetails);
        assertEquals("user", userDetails.getUsername());
        assertEquals("userPassword", userDetails.getPassword());
        
        // 普通用户应该只有用户管理权限
        assertEquals(1, userDetails.getAuthorities().size());
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        assertTrue(authorities.contains("system:user:view"));
        assertFalse(authorities.contains("system:user:add"));
        assertFalse(authorities.contains("system:role:view"));
    }

    @Test
    public void testLoadUserByUsernameForNonExistentUser() {
        when(userRepository.findOneByLogin("nonexistent")).thenReturn(Optional.empty());
        // 测试不存在的用户登录
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailService.loadUserByUsername("nonexistent");
        });
    }
} 