package com.z.module.system.service;

import com.z.module.system.domain.LoginLog;
import com.z.module.system.repository.LoginLogRepository;
import com.z.module.system.web.vo.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginLogServiceTest {

    @Mock
    private LoginLogRepository loginLogRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private LoginLogService loginLogService;

    private LoginVO loginVO;
    private LoginLog loginLog;

    @BeforeEach
    public void setUp() {
        // 创建测试登录数据
        loginVO = new LoginVO();
        loginVO.setUsername("testuser");
        loginVO.setPassword("password");
        loginVO.setRememberMe(true);
        loginVO.setCaptcha("1234");

        // 创建保存后返回的登录日志
        loginLog = new LoginLog();
        loginLog.setId(1L);
        loginLog.setLoginName("testuser");
        loginLog.setClientIp("127.0.0.1");
        loginLog.setOs("Windows");
        loginLog.setBrowser("Chrome");


    }

    @Test
    public void testSave() {
        // 模拟HttpServletRequest行为
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36");
        // 模拟repository行为
        when(loginLogRepository.save(any(LoginLog.class))).thenReturn(loginLog);
        // 执行测试
        LoginLog savedLog = loginLogService.save(loginVO, request);

        // 验证结果
        assertNotNull(savedLog);
        assertEquals(1L, savedLog.getId());
        assertEquals("testuser", savedLog.getLoginName());
        assertEquals("127.0.0.1", savedLog.getClientIp());
        assertEquals("Windows", savedLog.getOs());
        assertEquals("Chrome", savedLog.getBrowser());

        // 验证repository被调用
        verify(loginLogRepository).save(any(LoginLog.class));
    }
} 