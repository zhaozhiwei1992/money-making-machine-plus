
package com.z.module.report.service.checkrule.trans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaxFormulaTransformerTest {

    private MaxFormulaTransformer transformer = new MaxFormulaTransformer();

    @Test
    public void testTransform() {
        String expression = "MAX(A, B)";
        String transformed = transformer.transform(expression, null);
        assertEquals("#max(A, B)", transformed);
    }
}
