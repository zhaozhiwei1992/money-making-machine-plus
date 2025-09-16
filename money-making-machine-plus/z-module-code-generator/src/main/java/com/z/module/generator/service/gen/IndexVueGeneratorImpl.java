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
 * @Description: 前后端分离, vue列表页生成
 * @author: zhaozhiwei
 * @date: 2023/3/16 下午2:28
 * @version: V1.0
 */
@Service
public class IndexVueGeneratorImpl extends AbstractGeneratorTemplateImpl {
    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public String getTemplatePath() {
        return "templates/Index.vue.vm";
    }

    @Override
    public Map<String, Object> getTemplateData(Map<String, Object> table, List<Map<String, Object>> columns) {
        Map<String, Object> map = new HashMap<>();
        map.put("comments", table.get("comments"));
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        // 横线分隔
        map.put("pathName", StrUtil.toSymbolCase(className, '-').toLowerCase());

        final Map<String, Object> colJavaMapping = appConfiguration.getGenerator().getMapping();
        for (Map<String, Object> column : columns) {
            final String columnName = String.valueOf(column.get("columnName"));
            final String columnType = String.valueOf(column.get("columnType"));

            // 陀峰转 underline
            column.put("attr_name", StrUtil.toUnderlineCase(columnName));
            //列的数据类型，转换成Java类型
            String attrType = String.valueOf(colJavaMapping.getOrDefault(columnType, "unknowType"));
            column.put("attrType", attrType);
            column.put("showSearch", false);
            column.put("showForm", false);
            column.put("showDetail", false);
            column.put("comments", column.get("colComments"));
        }

        map.put("columns", columns);
        return map;
    }

    @Override
    public String getFileName(Map<String, Object> table) {
        //z-ui-admin-vue3/src/views/system/User/Index.vue
        String fileName =
                "src" + File.separator + "views" + File.separator + appConfiguration.getGenerator().getModule() + File.separator;
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        // resources/templates/module/list.js
        fileName += className + File.separator + "Index.vue";
        return fileName;
    }
}
