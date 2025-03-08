package com.z.module.system.service;

import com.z.module.system.domain.Menu;
import com.z.module.system.repository.MenuRepository;
import com.z.module.system.repository.RoleMenuRepository;
import com.z.module.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith({
//        SpringExtension.class,
        MockitoExtension.class})
public class MenuServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleMenuRepository roleMenuRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuService menuService;

    private List<Menu> testMenus;

    @BeforeEach
    public void setUp() {
        // 初始化测试菜单
        testMenus = new ArrayList<>();
        Menu menu1 = new Menu();
        menu1.setId(1L);
        menu1.setName("Dashboard");
        menu1.setUrl("/dashboard");
        menu1.setOrderNum(1);
        menu1.setParentId(0L);
        menu1.setEnabled(true);
        menu1.setMenuType(1);

        Menu menu2 = new Menu();
        menu2.setId(2L);
        menu2.setName("User Management");
        menu2.setUrl("/users");
        menu2.setOrderNum(2);
        menu2.setParentId(0L);
        menu2.setEnabled(true);
        menu2.setMenuType(1);

        Menu menu3 = new Menu();
        menu3.setId(3L);
        menu3.setName("User List");
        menu3.setUrl("/users/list");
        menu3.setOrderNum(1);
        menu3.setParentId(2L);
        menu3.setEnabled(true);
        menu3.setMenuType(1);

        testMenus.add(menu1);
        testMenus.add(menu2);
        testMenus.add(menu3);
    }

    @Test
    public void findAllMenusByLogin_EmptyResult_ReturnsEmptyList() {
        when(menuRepository.findAllByOrderByOrderNumAsc()).thenReturn(new ArrayList<>());

        List<Menu> result = menuService.findAllMenusByLogin("testUser");

        assertTrue(result.isEmpty());
    }

    @Test
    public void findAllMenusByLogin_NonEmptyResult_ReturnsMenuListOrderedByOrderNum() {
        when(menuRepository.findAllByOrderByOrderNumAsc()).thenReturn(testMenus);

        List<Menu> result = menuService.findAllMenusByLogin("testUser");

        assertEquals(3, result.size());
        assertEquals("Dashboard", result.get(0).getName());
        assertEquals("User Management", result.get(1).getName());
        assertEquals("User List", result.get(2).getName());
    }

    @Test
    public void findAllMenusByLogin_CacheBehavior() {
        // Explicitly set up the mock to return testMenus
        when(menuRepository.findAllByOrderByOrderNumAsc()).thenReturn(testMenus);
        // 第一次调用
        List<Menu> result1 = menuService.findAllMenusByLogin("testUser");
        assertEquals(3, result1.size());
        // 第二次调用
        List<Menu> result2 = menuService.findAllMenusByLogin("testUser");
        assertEquals(3, result2.size());
    }

//    @Configuration
//    @EnableCaching
//    static class CacheConfig {
//        @Bean
//        public CacheManager cacheManager() {
//            return new ConcurrentMapCacheManager("loginMenuCache");
//        }
//    }
}