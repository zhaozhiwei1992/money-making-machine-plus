
package com.z.module.report.service.checkrule.trans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IfFormulaTransformerTest {

    private IfFormulaTransformer transformer = new IfFormulaTransformer();

    @Test
    public void testTransform() {
        String expression = "IF(A>B, A, B)";
        String transformed = transformer.transform(expression, null);
        assertEquals("(A>B?A:B)", transformed);
    }
}
