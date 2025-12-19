
package com.z.module.report.service.checkrule.trans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModFormulaTransformerTest {

    private ModFormulaTransformer transformer = new ModFormulaTransformer();

    @Test
    public void testTransform() {
        String expression = "MOD(A, B)";
        String transformed = transformer.transform(expression, null);
        assertEquals("A%B", transformed);
    }
}
