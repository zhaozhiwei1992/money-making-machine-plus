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
 * @Description: 菜单脚本生成
 * @author: zhaozhiwei
 * @date: 2023/3/16 下午2:28
 * @version: V1.0
 */
@Service
public class MenuSqlGeneratorImpl extends AbstractGeneratorTemplateImpl {
    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public String getTemplatePath() {
        return "templates/menu.sql.vm";
    }

    @Override
    public Map<String, Object> getTemplateData(Map<String, Object> table, List<Map<String, Object>> columns) {
        Map<String, Object> map = new HashMap<>();
        map.put("comments", table.get("comments"));
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        // 横线分隔
        map.put("pathName", StrUtil.toSymbolCase(className, '-').toLowerCase());
        final String module = appConfiguration.getGenerator().getModule();
        map.put("module", module);
        return map;
    }

    @Override
    public String getFileName(Map<String, Object> table) {
        ///src/main/resources/liquibase/system/changelog/00000_init.sql
        String fileName =
                "main" + File.separator + "resources" + File.separator + "liquibase" + File.separator
                        + appConfiguration.getGenerator().getModule() + File.separator + "changelog" + File.separator;
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        fileName += className + "_menu.sql";
        return fileName;
    }
}
