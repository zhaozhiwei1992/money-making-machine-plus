package com.z.module.generator.service.gen;

import cn.hutool.core.util.StrUtil;
import com.z.module.generator.config.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: null.java
 * @Package: com.longtu.web.service.gen
 * @Description: 实体类生成实现
 * @author: zhaozhiwei
 * @date: 2023/3/16 下午2:30
 * @version: V1.0
 */
@Service
public class DomainGeneratorImpl extends AbstractGeneratorTemplateImpl {

    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public String getTemplatePath() {
        return "templates/Domain.java.vm";
    }

    @Override
    public Map<String, Object> getTemplateData(Map<String, Object> table, List<Map<String, Object>> columns) {
        Map<String, Object> map = new HashMap<>();
        map.put("basePackage", appConfiguration.getGenerator().getBasePackage());
        map.put("tableName", table.get("tableName"));
        map.put("comments", table.get("comments"));
        map.put("pk", table.get("pKey"));
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        map.put("className", className);

        final Map<String, Object> colJavaMapping = appConfiguration.getGenerator().getMapping();
        boolean hasBigDecimal = false;
        for (Map<String, Object> column : columns) {
            final String columnName = String.valueOf(column.get("columnName"));
            final String columnType = String.valueOf(column.get("columnType"));

            //列名转换成Java属性名
            String javaName = columnToJava(columnName);
            column.put("attrName", javaName);
            column.put("attr_name", StrUtil.toUnderlineCase(javaName));
            //列的数据类型，转换成Java类型
            String attrType = String.valueOf(colJavaMapping.getOrDefault(columnType, "unknowType"));
            column.put("attrType", attrType);
            if (!hasBigDecimal && attrType.equals("BigDecimal" )) {
                hasBigDecimal = true;
            }
        }
        
        map.put("columns", columns);
        map.put("hasBigDecimal", hasBigDecimal);
        return map;
    }

    @Override
    public String getFileName(Map<String, Object> table) {
        String fileName = super.getFileName(table);
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        fileName += "domain" + File.separator + className + ".java";
        return fileName;
    }
}
