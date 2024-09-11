package com.z.module.bpm.domain.convert;

import com.alibaba.fastjson.JSON;

import jakarta.persistence.AttributeConverter;
import java.util.Map;

/**
 * @Title: JpaConverterListJson
 * @Package com/z/module/bpm/domain/convert/JpaConverterListJson.java
 * @Description: Map和json字符串互转
 * @author zhaozhiwei
 * @date 2023/5/24 上午11:20
 * @version V1.0
 */
public class JpaMapJsonConverter implements AttributeConverter<Map<String, Object>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
        return JSON.toJSONString(stringObjectMap);
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String s) {
        return JSON.parseObject(s, Map.class);
    }
}