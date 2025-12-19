
package com.z.module.report.service.checkrule.trans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubStrFormulaTransformerTest {

    private SubStrFormulaTransformer transformer = new SubStrFormulaTransformer();

    @Test
    public void testTransform() {
        String expression = "SUBSTR(A, 1, 3)";
        String transformed = transformer.transform(expression, null);
        assertEquals("#substr(A, 1, 3)", transformed);
    }
}
