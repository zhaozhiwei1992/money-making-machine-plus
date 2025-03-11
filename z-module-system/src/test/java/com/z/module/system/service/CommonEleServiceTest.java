package com.z.module.system.service;

import com.z.module.system.domain.EleUnion;
import com.z.module.system.repository.EleUnionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommonEleServiceTest {

    @Mock
    private EleUnionRepository commonEleRepository;

    @InjectMocks
    private CommonEleService commonEleService;

    @BeforeEach
    public void setUp() {
        // 初始化测试数据
        List<EleUnion> commonEles = Arrays.asList(
                new EleUnion(1L, "Cat1", "Category1", "Ele1", "Element1", "Parent1", 1, true, true),
                new EleUnion(2L, "Cat2", "Category2", "Ele2", "Element2", "Parent2", 2, true, true)
        );

        // 模拟数据库查询
        when(commonEleRepository.findAll()).thenReturn(commonEles);
    }

    @Test
    public void testGetAllCommonEles() {
        List<EleUnion> all = commonEleService.findAll();
        assertEquals(2, all.size());
        assertEquals("Cat1", all.getFirst().getEleCatCode());
        assertEquals("Category1", all.getFirst().getEleCatName());
    }

    // 其他测试方法
}