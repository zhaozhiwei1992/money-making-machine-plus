
package com.z.module.generator.service.gen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceGeneratorImplTest {

    @InjectMocks
    private ResourceGeneratorImpl resourceGenerator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTemplatePath() {
        assertEquals("templates/Resource.java.vm", resourceGenerator.getTemplatePath());
    }

    @Test
    public void testGetTemplateData() {
        Map<String, Object> table = new HashMap<>();
        table.put("tableName", "test_table");

        Map<String, Object> data = resourceGenerator.getTemplateData(table, Collections.emptyList());

        assertEquals("TestTable", data.get("className"));
        assertEquals("testTable", data.get("classNameLowerFirst"));
        assertEquals("test-table", data.get("pathName"));
    }

    @Test
    public void testGetFileName() {
        Map<String, Object> table = new HashMap<>();
        table.put("tableName", "test_table");

        String fileName = resourceGenerator.getFileName(table);

        assertTrue(fileName.endsWith("web/rest/TestTableResource.java"));
    }
}
