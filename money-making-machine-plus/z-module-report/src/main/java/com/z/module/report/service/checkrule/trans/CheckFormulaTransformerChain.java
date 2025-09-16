package com.z.module.report.service.checkrule.trans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CheckFormulaTransformerChain {

    @Autowired
    private Map<String, CheckFormulaTransformer> transformerMap;

    public String transform(String data, Map<String, String> formatMapABC) {
        for (CheckFormulaTransformer transformer : transformerMap.values()) {
            data = transformer.transform(data, formatMapABC);
        }
        return data;
    }
}