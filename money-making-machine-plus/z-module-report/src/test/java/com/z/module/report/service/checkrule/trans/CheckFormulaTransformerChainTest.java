
package com.z.module.report.service.checkrule.trans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckFormulaTransformerChainTest {

    private CheckFormulaTransformerChain transformerChain;

    @BeforeEach
    public void setUp() {
        transformerChain = new CheckFormulaTransformerChain();
        transformerChain.setTransformers(Collections.singletonList(new AbsFormulaTransformer()));
    }

    @Test
    public void testTransform() {
        String expression = "ABS(C5)";
        String transformed = transformerChain.transform(expression, Collections.emptyMap());
        assertEquals("#abs(C5)", transformed);
    }
}
