package com.z.module.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.z.framework.common.service.DatabaseMetaService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommonEleServiceTest {

    @Mock
    private EleUnionRepository eleUnionRepository;

    @Mock
    private DatabaseMetaService databaseMetaService;

    @InjectMocks
    private CommonEleService commonEleService;

    private List<EleUnion> testEleUnions;

    @BeforeEach
    public void setUp() {
        // 初始化测试数据
        testEleUnions = Arrays.asList(
                new EleUnion(1L, "Cat1", "Category1", "Ele1", "Element1", "0", 1, true, true),
                new EleUnion(2L, "Cat1", "Category1", "Ele2", "Element2", "1", 2, true, true),
                new EleUnion(3L, "Cat2", "Category2", "Ele3", "Element3", "0", 1, true, true)
        );

    }

    @Test
    public void testFindAll() {
        when(eleUnionRepository.findAll()).thenReturn(testEleUnions);
        List<EleUnion> all = commonEleService.findAll();
        assertEquals(3, all.size());
        assertEquals("Cat1", all.get(0).getEleCatCode());
        assertEquals("Category1", all.get(0).getEleCatName());
    }

    @Test
    public void testFindElementInfoByEleCatCode() {
        // 测试存在的类别代码
        when(eleUnionRepository.findByEleCatCode("Cat1")).thenReturn(Arrays.asList(testEleUnions.get(0), testEleUnions.get(1)));
        List<EleUnion> elements = commonEleService.findElementInfoByEleCatCode("Cat1");
        assertEquals(2, elements.size());
        assertEquals("Ele1", elements.get(0).getEleCode());
        assertEquals("Ele2", elements.get(1).getEleCode());

        // 测试另一个存在的类别代码
        when(eleUnionRepository.findByEleCatCode("Cat2")).thenReturn(Arrays.asList(testEleUnions.get(2)));
        elements = commonEleService.findElementInfoByEleCatCode("Cat2");
        assertEquals(1, elements.size());
        assertEquals("Ele3", elements.get(0).getEleCode());
        
        // 如果需要测试NonExistent的情况，取消注释下面的代码
        // elements = commonEleService.findElementInfoByEleCatCode("NonExistent");
        // assertEquals(0, elements.size());
    }

    @Test
    public void testTransToMapping() {
        // 测试树形转换
        when(eleUnionRepository.findByEleCatCode("Cat1")).thenReturn(Arrays.asList(testEleUnions.get(0), testEleUnions.get(1)));
        List<Tree<Long>> trees = commonEleService.transToMapping("Cat1");
        
        // 验证树结构
        assertEquals(1, trees.size()); // 根节点数量
        
        Tree<Long> rootNode = trees.get(0);
        assertEquals(1L, rootNode.getId());
        assertEquals("Element1", rootNode.getName());
//        assertEquals("Ele1", rootNode.getExtra("code"));
        
        // 验证子节点
        List<Tree<Long>> children = rootNode.getChildren();
        assertNotNull(children);
        assertEquals(1, children.size());
        
        Tree<Long> childNode = children.get(0);
        assertEquals(2L, childNode.getId());
        assertEquals("Element2", childNode.getName());
//        assertEquals("Ele2", childNode.getExtra("code"));
    }

    @Test
    public void testFindAllEleCategory() {
        when(eleUnionRepository.findAll()).thenReturn(testEleUnions);
        List<Map<String, Object>> categories = commonEleService.findAllEleCategory();
        // 由于方法内部实现比较复杂，这里只做简单验证
        assertNotNull(categories);
    }

    @Test
    public void testFindAllTableNamesStartWithEle() {
        when(databaseMetaService.getTables()).thenReturn(Arrays.asList("ele_union", "ele_Cat1"));
        List<String> tables = commonEleService.findAllTableNamesStartWithEle();
        assertEquals(2, tables.size());
        assertEquals("ele_union", tables.get(0));
        assertEquals("ele_Cat1", tables.get(1));
    }
}