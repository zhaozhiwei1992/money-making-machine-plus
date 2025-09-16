package com.z.module.bpm.domain.convert;

import com.alibaba.fastjson.JSON;

import jakarta.persistence.AttributeConverter;
import java.util.List;

/**
 * @Title: JpaConverterListJson
 * @Package com/z/module/bpm/domain/convert/JpaConverterListJson.java
 * @Description: List<String>和json字符串互转
 * @author zhaozhiwei
 * @date 2023/5/24 上午11:20
 * @version V1.0
 */
public class JpaListStringJsonConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List o) {
        return JSON.toJSONString(o);
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        return JSON.parseArray(s, String.class);
    }
}