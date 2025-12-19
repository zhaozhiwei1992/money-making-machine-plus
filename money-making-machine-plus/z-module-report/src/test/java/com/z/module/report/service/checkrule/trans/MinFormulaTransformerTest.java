
package com.z.module.report.service.checkrule.trans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinFormulaTransformerTest {

    private MinFormulaTransformer transformer = new MinFormulaTransformer();

    @Test
    public void testTransform() {
        String expression = "MIN(A, B)";
        String transformed = transformer.transform(expression, null);
        assertEquals("#min(A, B)", transformed);
    }
}
