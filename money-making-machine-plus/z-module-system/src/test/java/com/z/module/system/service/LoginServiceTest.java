package com.z.module.system.service;

import com.z.module.system.domain.UserOpenId;
import com.z.module.system.repository.UserOpenIdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache tokenWriteListCache;

    @Mock
    private Cache.ValueWrapper valueWrapper;

    @Mock
    private UserOpenIdRepository userOpenIdRepository;

    @InjectMocks
    private LoginService loginService;

    @Test
    public void testAddTokenWriteListWithEmptyCache() {
        // 模拟cacheManager行为，只在这个测试方法中需要
        when(cacheManager.getCache("tokenWriteListCache")).thenReturn(tokenWriteListCache);
        // 模拟缓存为空的情况
        when(tokenWriteListCache.get("tokenWriteList")).thenReturn(null);
        
        // 执行测试
        loginService.addTokenWriteList("test-token");
        
        // 验证结果
        ArgumentCaptor<List<String>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(tokenWriteListCache).put(eq("tokenWriteList"), listCaptor.capture());
        
        List<String> capturedList = listCaptor.getValue();
        assertEquals(1, capturedList.size());
        assertEquals("test-token", capturedList.get(0));
    }

    @Test
    public void testAddTokenWriteListWithExistingCache() {
        // 模拟cacheManager行为，只在这个测试方法中需要
        when(cacheManager.getCache("tokenWriteListCache")).thenReturn(tokenWriteListCache);
        // 模拟缓存中已有数据
        List<String> existingList = new ArrayList<>();
        existingList.add("existing-token");
        
        when(tokenWriteListCache.get("tokenWriteList")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(existingList);
        
        // 执行测试
        loginService.addTokenWriteList("new-token");
        
        // 验证结果
        ArgumentCaptor<List<String>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(tokenWriteListCache).put(eq("tokenWriteList"), listCaptor.capture());
        
        List<String> capturedList = listCaptor.getValue();
        assertEquals(2, capturedList.size());
        assertEquals("existing-token", capturedList.get(0));
        assertEquals("new-token", capturedList.get(1));
    }

    @Test
    public void testSaveOpenidUserLogin() {
        // 执行测试
        loginService.saveOpenidUserLogin("test-openid", "test-login");
        
        // 验证删除旧数据的调用
        verify(userOpenIdRepository).deleteByLogin("test-login");
        verify(userOpenIdRepository).deleteByOpenId("test-openid");
        
        // 验证保存新数据的调用
        ArgumentCaptor<UserOpenId> userOpenIdCaptor = ArgumentCaptor.forClass(UserOpenId.class);
        verify(userOpenIdRepository).save(userOpenIdCaptor.capture());
        
        UserOpenId capturedUserOpenId = userOpenIdCaptor.getValue();
        assertEquals("test-login", capturedUserOpenId.getLogin());
        assertEquals("test-openid", capturedUserOpenId.getOpenId());
    }
} 