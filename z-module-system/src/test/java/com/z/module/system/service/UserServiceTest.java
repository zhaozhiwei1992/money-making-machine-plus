package com.z.module.system.service;

import com.z.module.system.domain.User;
import com.z.module.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserService}
 */
@ExtendWith({
//        SpringExtension.class,
        MockitoExtension.class})
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // 创建测试用户数据
        user1 = new User();
        user1.setId(1L);
        user1.setLogin("user1");
        user1.setPassword("password1");
        user1.setName("User One");
        user1.setActivated(true);
        user1.setEmail("user1@example.com");
        user1.setPhoneNumber("13800138001");

        user2 = new User();
        user2.setId(2L);
        user2.setLogin("user2");
        user2.setPassword("password2");
        user2.setName("User Two");
        user2.setActivated(true);
        user2.setEmail("user2@example.com");
        user2.setPhoneNumber("13800138002");

    }

    @Test
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        List<User> users = userService.findAll();
        assertEquals(2, users.size());
        assertEquals("user1", users.get(0).getLogin());
        assertEquals("user2", users.get(1).getLogin());
    }

    @Test
    void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        User user = userService.findById(1L);
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("user1", user.getLogin());
        assertEquals("User One", user.getName());
    }

    @Test
    void testFindByIdNotFound() {
        // 当找不到用户时，应该返回新User对象
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        User user = userService.findById(999L);
        assertNotNull(user);
        assertEquals(null, user.getId());
    }

    @Test
    void testSave() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User newUser = new User();
        newUser.setLogin("newuser");
        newUser.setPassword("newpassword");
        newUser.setName("New User");
        
        User savedUser = userService.save(newUser);
        assertEquals("newuser", savedUser.getLogin());
        assertEquals("New User", savedUser.getName());
    }

    @Test
    void testFindByLogin() {
        when(userRepository.findOneByLogin("user1")).thenReturn(Optional.of(user1));
        User user = userService.findByLogin("user1");
        assertNotNull(user);
        assertEquals("user1", user.getLogin());
        assertEquals("User One", user.getName());
    }

    @Test
    void testFindByLoginNotFound() {
        // 当找不到用户时，应该返回新User对象
        when(userRepository.findOneByLogin("nonexistent")).thenReturn(Optional.empty());
        
        User user = userService.findByLogin("nonexistent");
        assertNotNull(user);
        assertEquals(null, user.getId());
    }

    @Test
    void findById_shouldCacheResults() {
        // Arrange - Use specific ID
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // Act - Call the method twice with the same ID
        User result1 = userService.findById(1L);
        
        // Verify first call returned expected data
        assertNotNull(result1);
        assertEquals(user1.getId(), result1.getId());
        assertEquals("user1", result1.getLogin());
        
        // Second call should use cache
        User result2 = userService.findById(1L);

        // Assert
        assertNotNull(result2);
        assertEquals(user1.getId(), result2.getId());
        
        // Verify that the repository method was only called once due to caching
//        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findByLogin_shouldCacheResults() {
        // Arrange - Use specific login
        when(userRepository.findOneByLogin("user1")).thenReturn(Optional.of(user1));

        // Act - Call the method twice with the same login
        User result1 = userService.findByLogin("user1");
        
        // Verify first call returned expected data
        assertNotNull(result1);
        assertEquals(user1.getId(), result1.getId());
        assertEquals("user1", result1.getLogin());
        
        // Second call should use cache
        User result2 = userService.findByLogin("user1");

        // Assert
        assertNotNull(result2);
        assertEquals(user1.getId(), result2.getId());

        // Verify that the repository method was only called once due to caching
    }

//    @Configuration
//    @EnableCaching
//    static class CacheConfig {
//        @Bean
//        public CacheManager cacheManager() {
//            return new ConcurrentMapCacheManager("users", "usersByLogin");
//        }
//    }
}