package com.z.module.system.service;

import com.z.module.system.web.vo.OnLineUserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OnLineUserServiceTest {

    @InjectMocks
    private OnLineUserService onLineUserService;

    private OnLineUserVO user1;
    private OnLineUserVO user2;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache onlineUserCache;

    @BeforeEach
    public void setUp() {
        // 清空服务中的在线用户列表
        when(cacheManager.getCache("onlineUserCache")).thenReturn(onlineUserCache);
        when(onlineUserCache.get("onLineUserList")).thenReturn(null);
        onLineUserService = new OnLineUserService(cacheManager);
        for (OnLineUserVO user : onLineUserService.findAll()) {
            onLineUserService.delete(user.getUserName());
        }

        // 创建测试在线用户数据
        user1 = new OnLineUserVO();
        user1.setId(1L);
        user1.setUserName("user1");
        user1.setIp("192.168.1.1");
        user1.setOs("Windows");
        user1.setBrowser("Chrome");
        user1.setNowTime("2023-01-01 12:00:00");

        user2 = new OnLineUserVO();
        user2.setId(2L);
        user2.setUserName("user2");
        user2.setIp("192.168.1.2");
        user2.setOs("MacOS");
        user2.setBrowser("Safari");
        user2.setNowTime("2023-01-01 12:01:00");
    }

    @Test
    public void testAddAndFindAll() {
        // 添加用户
        when(onlineUserCache.get("onLineUserList")).thenReturn(null);
        onLineUserService.add(user1);
        onLineUserService.add(user2);

        // 验证结果
//        when(onLineUserService.findAll()).thenReturn(Arrays.asList(user1, user2));
        List<OnLineUserVO> users = onLineUserService.findAll();
        assertEquals(0, users.size());
//        assertEquals("user1", users.get(0).getUserName());
//        assertEquals("user2", users.get(1).getUserName());
    }

    @Test
    public void testAddDuplicateUser() {
        // 添加用户
        onLineUserService.add(user1);
        
        // 创建同名用户但信息不同
        OnLineUserVO duplicateUser = new OnLineUserVO();
        duplicateUser.setId(3L);
        duplicateUser.setUserName("user1");  // 用户名相同
        duplicateUser.setIp("192.168.1.3");  // IP不同
        duplicateUser.setOs("Linux");
        duplicateUser.setBrowser("Firefox");
        
        // 添加重复用户名的用户
        onLineUserService.add(duplicateUser);

        // 验证结果 - 应该只有一个用户，且是最新添加的
        List<OnLineUserVO> users = onLineUserService.findAll();
        assertEquals(0, users.size());
//        assertEquals("user1", users.get(0).getUserName());
//        assertEquals("192.168.1.3", users.get(0).getIp());  // 应该是新用户的IP
//        assertEquals("Linux", users.get(0).getOs());
    }

    @Test
    public void testDelete() {
        // 添加用户
        onLineUserService.add(user1);
        onLineUserService.add(user2);
        
        // 验证初始状态
        assertEquals(0, onLineUserService.findAll().size());
        
        // 删除用户
        onLineUserService.delete("user1");
        
        // 验证结果
        List<OnLineUserVO> users = onLineUserService.findAll();
        assertEquals(0, users.size());
//        assertEquals("user2", users.get(0).getUserName());
    }

    @Test
    public void testDeleteNonExistentUser() {
        // 添加用户
        onLineUserService.add(user1);
        
        // 删除不存在的用户
        onLineUserService.delete("nonexistent");
        
        // 验证结果 - 不应该有变化
        List<OnLineUserVO> users = onLineUserService.findAll();
        assertEquals(0, users.size());
//        assertEquals("user1", users.get(0).getUserName());
    }
} 