
package com.z.module.generator.service.gen;

import com.z.module.generator.config.AppConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    @Mock
    private AppConfiguration.GeneratorProperties generatorProperties;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(appConfiguration.getGenerator()).thenReturn(generatorProperties);
    }

    @Test
    public void testGetTemplatePath() {
        assertEquals("templates/Repository.java.vm", repositoryGenerator.getTemplatePath());
    }

    @Test
    public void testGetTemplateData() {
        Map<String, Object> table = new HashMap<>();
        table.put("tableName", "test_table");

        when(generatorProperties.getBasePackage()).thenReturn("com.z.test");

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
