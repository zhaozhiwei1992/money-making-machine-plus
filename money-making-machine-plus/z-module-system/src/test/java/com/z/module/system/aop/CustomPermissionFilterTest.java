
package com.z.module.system.aop;

import com.z.module.system.domain.User;
import com.z.module.system.repository.MenuRepository;
import com.z.module.system.repository.RoleMenuRepository;
import com.z.module.system.repository.UserAuthorityRepository;
import com.z.module.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class CustomPermissionFilterTest {

    @Mock
    private UserAuthorityRepository userAuthorityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private RoleMenuRepository roleMenuRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CustomPermissionFilter customPermissionFilter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHasPermission_whitelistedUrl() {
        when(request.getRequestURI()).thenReturn("/api/menus/route");
        assertTrue(customPermissionFilter.hasPermission(authentication, request));
    }

    @Test
    public void testHasPermission_adminUser() {
        when(request.getRequestURI()).thenReturn("/api/some_protected_url");
        when(authentication.getPrincipal()).thenReturn("admin");
        assertTrue(customPermissionFilter.hasPermission(authentication, request));
    }

    @Test
    public void testHasPermission_userNotFound() {
        when(request.getRequestURI()).thenReturn("/api/some_protected_url");
        when(authentication.getPrincipal()).thenReturn("nonexistent_user");
        when(userRepository.findOneByLogin("nonexistent_user")).thenReturn(Optional.empty());
        assertFalse(customPermissionFilter.hasPermission(authentication, request));
    }

    @Test
    public void testHasPermission_noPermissions() {
        User user = new User();
        user.setId(1L);
        user.setLogin("test_user");

        when(request.getRequestURI()).thenReturn("/api/some_protected_url");
        when(authentication.getPrincipal()).thenReturn("test_user");
        when(userRepository.findOneByLogin("test_user")).thenReturn(Optional.of(user));
        assertFalse(customPermissionFilter.hasPermission(authentication, request));
    }
}
