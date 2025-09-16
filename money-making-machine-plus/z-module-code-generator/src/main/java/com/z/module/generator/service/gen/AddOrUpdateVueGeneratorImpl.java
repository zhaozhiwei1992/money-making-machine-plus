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
 * @Description: 前后端分离, AddOrUpdate.Vue明细页生成
 * @author: zhaozhiwei
 * @date: 2023/3/16 下午2:28
 * @version: V1.0
 */
@Service
public class AddOrUpdateVueGeneratorImpl extends AbstractGeneratorTemplateImpl{
    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public String getTemplatePath() {
        return "templates/AddOrUpdate.vue.vm";
    }

    @Override
    public Map<String, Object> getTemplateData(Map<String, Object> table, List<Map<String, Object>> columns) {
        Map<String, Object> map = new HashMap<>();
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        // 横线分隔
        map.put("pathName", StrUtil.toSymbolCase(className, '-').toLowerCase());
        map.put("module", appConfiguration.getGenerator().getModule());
        for (Map<String, Object> column : columns) {
            final String columnName = String.valueOf(column.get("columnName"));

            //列名转换成Java属性名
            String javaName = columnToJava(columnName);
            column.put("attrName", javaName);
            column.put("attr_name", StrUtil.toUnderlineCase(javaName));
        }
        map.put("columns", columns);

        return map;
    }

    @Override
    public String getFileName(Map<String, Object> table) {
        //z-ui-admin-vue3/src/views/system/User/components/AddOrUpdate.vue
        String fileName = "src" + File.separator + "views" + File.separator + appConfiguration.getGenerator().getModule() + File.separator;
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        fileName += className + File.separator + "components" + File.separator + "AddOrUpdate.vue";
        return fileName;
    }
}
