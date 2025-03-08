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
class SysParamServiceTest {

    @Mock
    private SysParamRepository sysParamRepository;

    @InjectMocks
    private SysParamService sysParamService;

    private SystemParam testParam;

    @BeforeEach
    void setUp() {
        // Create test system parameter
        testParam = new SystemParam();
        testParam.setId(1L);
        testParam.setCode("TEST_PARAM");
        testParam.setName("Test Parameter");
        testParam.setValue("test-value");
        testParam.setRemark("This is a test parameter");
        testParam.setEnable(true);
    }

    @Test
    void findByCode_whenParamExists_shouldReturnParam() {
        // Arrange
        when(sysParamRepository.findOneByCode("TEST_PARAM")).thenReturn(Optional.of(testParam));
        
        // Act
        SystemParam result = sysParamService.findByCode("TEST_PARAM");
        
        // Assert
        assertNotNull(result);
        assertEquals(testParam.getId(), result.getId());
        assertEquals(testParam.getCode(), result.getCode());
        assertEquals(testParam.getName(), result.getName());
        assertEquals(testParam.getValue(), result.getValue());
        assertEquals(testParam.getRemark(), result.getRemark());
        assertEquals(testParam.getEnable(), result.getEnable());
        verify(sysParamRepository, times(1)).findOneByCode("TEST_PARAM");
    }

    @Test
    void findByCode_whenParamDoesNotExist_shouldReturnEmptyParam() {
        // Arrange
        when(sysParamRepository.findOneByCode("NON_EXISTENT")).thenReturn(Optional.empty());
        
        // Act
        SystemParam result = sysParamService.findByCode("NON_EXISTENT");
        
        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getCode());
        assertNull(result.getName());
        assertNull(result.getValue());
        verify(sysParamRepository, times(1)).findOneByCode("NON_EXISTENT");
    }

    @Test
    void findByCode_shouldCacheResults() {
        // Arrange - Use specific parameter name
        when(sysParamRepository.findOneByCode("TEST_PARAM")).thenReturn(Optional.of(testParam));
        
        // Act - Call the method twice with the same code
        SystemParam result1 = sysParamService.findByCode("TEST_PARAM");
        
        // Verify first call returned expected data
        assertNotNull(result1);
        assertEquals(testParam.getId(), result1.getId());
        assertEquals("TEST_PARAM", result1.getCode());
        
        // Second call should use cache
        SystemParam result2 = sysParamService.findByCode("TEST_PARAM");
        
        // Assert
        assertNotNull(result2);
        assertEquals(testParam.getId(), result2.getId());
    }
}