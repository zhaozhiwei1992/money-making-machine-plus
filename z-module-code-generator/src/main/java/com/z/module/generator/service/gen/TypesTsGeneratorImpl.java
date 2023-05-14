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
 * @Description: 前端类型ts
 * @author: zhaozhiwei
 * @date: 2023/3/16 下午2:30
 * @version: V1.0
 */
@Service
public class TypesTsGeneratorImpl extends AbstractGeneratorTemplateImpl {

    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public String getTemplatePath() {
        return "templates/types.ts.vm";
    }

    @Override
    public Map<String, Object> getTemplateData(Map<String, Object> table, List<Map<String, Object>> columns) {
        Map<String, Object> map = new HashMap<>();
        map.put("comments", table.get("comments"));

        final Map<String, Object> colJavaMapping = appConfiguration.getGenerator().getMapping();
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
            if (attrType.equals("BigDecimal" )) {
                column.put("attrType", "number");
            }else if("String".equals(attrType)){
                column.put("attrType", "string");
            }else if("Boolean".equals(attrType)){
                column.put("attrType", "boolean");
            }else{
                column.put("attrType", "any");
            }
        }
        
        map.put("columns", columns);
        return map;
    }

    @Override
    public String getFileName(Map<String, Object> table) {
        //z-ui-admin-vue3/src/api/system/user/types.ts
        String fileName = "src" + File.separator + "api" + File.separator + appConfiguration.getGenerator().getModule() + File.separator;
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        // resources/templates/module/list.js
        fileName += StrUtil.toSymbolCase(className, '-').toLowerCase() + File.separator + "types.ts";
        return fileName;
    }
}
