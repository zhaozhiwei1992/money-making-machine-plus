
package com.z.module.generator.service;

import com.z.module.generator.config.SpringUtil;
import com.z.module.generator.service.gen.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class GeneratorServiceTest {

    private GeneratorService generatorService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Generator mockGenerator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        generatorService = new GeneratorService();
        new SpringUtil().setApplicationContext(applicationContext);
    }

    @Test
    public void testGeneratorCode() throws IOException {
        Map<String, Object> table = new HashMap<>();
        table.put("tableName", "test_table");

        when(mockGenerator.generatorCode(table, Collections.emptyList())).thenReturn("test code");
        when(mockGenerator.getFileName(table)).thenReturn("test.java");

        Map<String, Generator> beans = new HashMap<>();
        beans.put("mockGenerator", mockGenerator);

        when(applicationContext.getBeansOfType(Generator.class)).thenReturn(beans);

        byte[] result = generatorService.generatorCode(Collections.singletonList(table), new HashMap<>());

        assertNotNull(result);
        assertTrue(result.length > 0);

        // Verify zip content
        try (ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(result))) {
            assertNotNull(zipIn.getNextEntry());
        }
    }
}
