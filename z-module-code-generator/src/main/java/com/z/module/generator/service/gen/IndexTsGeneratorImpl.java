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
 * @Description: 前后端分离, 前端api生成 Typescript
 * @author: zhaozhiwei
 * @date: 2023/3/16 下午2:28
 * @version: V1.0
 */
@Service
public class IndexTsGeneratorImpl extends AbstractGeneratorTemplateImpl{
    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public String getTemplatePath() {
        return "templates/index.ts.vm";
    }

    @Override
    public Map<String, Object> getTemplateData(Map<String, Object> table, List<Map<String, Object>> columns) {
        Map<String, Object> map = new HashMap<>();
        map.put("comments", table.get("comments"));
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        // 横线分隔 /api/users
        map.put("pathName", StrUtil.toSymbolCase(className, '-').toLowerCase() + "s");
        return map;
    }

    @Override
    public String getFileName(Map<String, Object> table) {
        //z-ui-admin-vue3/src/api/system/user/index.ts
        String fileName = "src" + File.separator + "api" + File.separator + appConfiguration.getGenerator().getModule() + File.separator;
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        // resources/templates/module/list.js
        fileName += StrUtil.toSymbolCase(className, '-').toLowerCase() + File.separator + "index.ts";
        return fileName;
    }
}
