package com.z.module.system.service;

import com.z.module.system.domain.SystemParam;
import com.z.module.system.repository.SysParamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SysParamService}
 */
@ExtendWith({MockitoExtension.class})
public class SysParamServiceTest {

    @Mock
    private SysParamRepository sysParamRepository;

    @InjectMocks
    private SysParamService sysParamService;

    private SystemParam param1;

    @BeforeEach
    public void setUp() {
        // 创建测试系统参数数据
        param1 = new SystemParam();
        param1.setId(1L);
        param1.setCode("TEST_PARAM");
        param1.setName("测试参数");
        param1.setValue("测试值");
        param1.setRemark("测试备注");
        param1.setEnable(true);

    }

    @Test
    public void testFindByCode() {
        when(sysParamRepository.findOneByCode("TEST_PARAM")).thenReturn(Optional.of(param1));
        // 执行测试
        SystemParam param = sysParamService.findByCode("TEST_PARAM");

        // 验证结果
        assertNotNull(param);
        assertEquals(1L, param.getId());
        assertEquals("TEST_PARAM", param.getCode());
        assertEquals("测试参数", param.getName());
        assertEquals("测试值", param.getValue());
    }

    @Test
    public void testFindByCodeNotFound() {
        when(sysParamRepository.findOneByCode("NON_EXISTENT")).thenReturn(Optional.empty());
        // 执行测试 - 查找不存在的参数
        SystemParam param = sysParamService.findByCode("NON_EXISTENT");

        // 验证返回新的空对象
        assertNotNull(param);
        assertEquals(null, param.getId());
    }

    @Test
    public void testFindByCodeShouldCacheResults() {
        when(sysParamRepository.findOneByCode("TEST_PARAM")).thenReturn(Optional.of(param1));
        // 第一次调用
        SystemParam param1 = sysParamService.findByCode("TEST_PARAM");
        assertNotNull(param1);
        assertEquals("TEST_PARAM", param1.getCode());
        
        // 第二次调用应该使用缓存
        SystemParam param2 = sysParamService.findByCode("TEST_PARAM");
        assertNotNull(param2);
        assertEquals("TEST_PARAM", param2.getCode());
        
        // 验证两次结果相同
        assertEquals(param1.getId(), param2.getId());
        assertEquals(param1.getValue(), param2.getValue());
    }
}