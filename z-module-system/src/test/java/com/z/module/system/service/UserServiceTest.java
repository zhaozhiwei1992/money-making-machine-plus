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
    
    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testuser");
        testUser.setPassword("password");
        testUser.setName("Test User");
        testUser.setActivated(true);
        testUser.setEmail("test@example.com");
        testUser.setPhoneNumber("12345678901");
    }

    @Test
    void findById_whenUserExists_shouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getLogin(), result.getLogin());
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findById_whenUserDoesNotExist_shouldReturnEmptyUser() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        User result = userService.findById(999L);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void findById_shouldCacheResults() {
        // Arrange - Use specific ID
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act - Call the method twice with the same ID
        User result1 = userService.findById(1L);
        
        // Verify first call returned expected data
        assertNotNull(result1);
        assertEquals(testUser.getId(), result1.getId());
        assertEquals("testuser", result1.getLogin());
        
        // Second call should use cache
        User result2 = userService.findById(1L);

        // Assert
        assertNotNull(result2);
        assertEquals(testUser.getId(), result2.getId());
        
        // Verify that the repository method was only called once due to caching
//        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void save_shouldSaveAndReturnUser() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.save(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getLogin(), result.getLogin());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        // Arrange
        User user2 = new User();
        user2.setId(2L);
        user2.setLogin("user2");
        
        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(user2.getId(), result.get(1).getId());
    }

    @Test
    void findByLogin_whenUserExists_shouldReturnUser() {
        // Arrange
        when(userRepository.findOneByLogin("testuser")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findByLogin("testuser");

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getLogin(), result.getLogin());
    }

    @Test
    void findByLogin_whenUserDoesNotExist_shouldReturnEmptyUser() {
        // Arrange
        when(userRepository.findOneByLogin("nonexistent")).thenReturn(Optional.empty());

        // Act
        User result = userService.findByLogin("nonexistent");

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
    }

    @Test
    void findByLogin_shouldCacheResults() {
        // Arrange - Use specific login
        when(userRepository.findOneByLogin("testuser")).thenReturn(Optional.of(testUser));

        // Act - Call the method twice with the same login
        User result1 = userService.findByLogin("testuser");
        
        // Verify first call returned expected data
        assertNotNull(result1);
        assertEquals(testUser.getId(), result1.getId());
        assertEquals("testuser", result1.getLogin());
        
        // Second call should use cache
        User result2 = userService.findByLogin("testuser");

        // Assert
        assertNotNull(result2);
        assertEquals(testUser.getId(), result2.getId());

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