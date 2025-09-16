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
 * @Description: 生成资源服务类
 * @author: zhaozhiwei
 * @date: 2023/3/16 下午2:28
 * @version: V1.0
 */
@Service
public class ResourceGeneratorImpl extends AbstractGeneratorTemplateImpl{

    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public String getTemplatePath() {
        return "templates/Resource.java.vm";
    }

    @Override
    public Map<String, Object> getTemplateData(Map<String, Object> table, List<Map<String, Object>> columns) {
        Map<String, Object> map = new HashMap<>();
        map.put("comments", table.get("comments"));
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        map.put("className", className);
        // 首字母小写
        map.put("classNameLowerFirst", StrUtil.lowerFirst(className));
        // 全部小写 - 分割
        map.put("pathName", StrUtil.toSymbolCase(className, '-').toLowerCase());

        map.put("moduleName", "resource");
        return map;
    }

    @Override
    public String getFileName(Map<String, Object> table) {
        String fileName = super.getFileName(table);
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        fileName += "web" + File.separator + "rest" + File.separator + className + "Resource.java";
        return fileName;
    }
}
