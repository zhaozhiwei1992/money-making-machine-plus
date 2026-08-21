
package com.z.module.generator.service.gen;

import com.z.module.generator.config.AppConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class RepositoryGeneratorImplTest {

    @InjectMocks
    private RepositoryGeneratorImpl repositoryGenerator;

    @Mock
    private AppConfiguration appConfiguration;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // @InjectMocks 只注入子类声明的字段；父类 AbstractGeneratorTemplateImpl 也有同名
        // appConfiguration 字段且为 null，getFileName/tableToJava 等父类方法依赖它，需反射补注入
        injectParentAppConfiguration();
        // stub getGenerator() 返回真实 Generator（非 mock 链），basePackage 供模板数据使用
        AppConfiguration.Generator generator = new AppConfiguration.Generator();
        generator.setBasePackage("com.z.test");
        when(appConfiguration.getGenerator()).thenReturn(generator);
    }

    private void injectParentAppConfiguration() {
        try {
            Field field = AbstractGeneratorTemplateImpl.class.getDeclaredField("appConfiguration");
            field.setAccessible(true);
            field.set(repositoryGenerator, appConfiguration);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("注入父类 appConfiguration 失败", e);
        }
    }

    @Test
    public void testGetTemplatePath() {
        assertEquals("templates/Repository.java.vm", repositoryGenerator.getTemplatePath());
    }

    @Test
    public void testGetTemplateData() {
        Map<String, Object> table = new HashMap<>();
        table.put("tableName", "test_table");

        Map<String, Object> data = repositoryGenerator.getTemplateData(table, Collections.emptyList());

        assertEquals("com.z.test", data.get("basePackage"));
        assertEquals("TestTable", data.get("className"));
    }

    @Test
    public void testGetFileName() {
        Map<String, Object> table = new HashMap<>();
        table.put("tableName", "test_table");

        String fileName = repositoryGenerator.getFileName(table);

        assertTrue(fileName.endsWith("repository/TestTableRepository.java"));
    }
}
