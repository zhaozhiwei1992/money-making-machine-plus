package com.z.module.generator.service.gen;

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
 * @Description: jpa持久化仓储生成实现
 * @author: zhaozhiwei
 * @date: 2023/3/16 下午2:44
 * @version: V1.0
 */
@Service
public class RepositoryGeneratorImpl extends AbstractGeneratorTemplateImpl{
    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public String getTemplatePath() {
        return "templates/Repository.java.vm";
    }

    @Override
    public Map<String, Object> getTemplateData(Map<String, Object> table, List<Map<String, Object>> columns) {
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", table.get("tableName"));
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        map.put("className", className);
        map.put("basePackage", appConfiguration.getGenerator().getBasePackage());
        return map;
    }

    @Override
    public String getFileName(Map<String, Object> table) {
        String fileName = super.getFileName(table);
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        fileName += "repository" + File.separator + className + "Repository.java";
        return fileName;
    }
}
