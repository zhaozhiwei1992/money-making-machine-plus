package com.z.module.report.service.extractrule.trans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExtractFormulaTransformerChain {

    @Autowired
    private Map<String, ExtractFormulaTransformer> transformerMap;

    public String transform(String data, Map<String, String> formatMapABC) {
        for (ExtractFormulaTransformer transformer : transformerMap.values()) {
            data = transformer.transform(data, formatMapABC);
        }
        return data;
    }
}