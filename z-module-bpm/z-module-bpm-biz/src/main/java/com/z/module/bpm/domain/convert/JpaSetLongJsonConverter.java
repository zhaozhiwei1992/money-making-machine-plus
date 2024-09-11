package com.z.module.bpm.domain.convert;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;

import jakarta.persistence.AttributeConverter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Title: JpaConverterListJson
 * @Package com/z/module/bpm/domain/convert/JpaConverterListJson.java
 * @Description: List和json字符串互转
 * @author zhaozhiwei
 * @date 2023/5/24 上午11:20
 * @version V1.0
 */
public class JpaSetLongJsonConverter implements AttributeConverter<Set<Long>, String> {
    @Override
    public String convertToDatabaseColumn(Set<Long> o) {
        return JSON.toJSONString(o);
    }

    @Override
    public Set<Long> convertToEntityAttribute(String s) {
        final List<Long> longs = JSON.parseArray(s, Long.class);
        return new HashSet<>(longs);
    }
}