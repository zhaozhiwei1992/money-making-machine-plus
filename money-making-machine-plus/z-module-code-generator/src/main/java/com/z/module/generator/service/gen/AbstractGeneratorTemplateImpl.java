package com.z.module.generator.service.gen;

import com.z.module.generator.config.AppConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.StringWriter;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: null.java
 * @Package: com.longtu.web.service.gen
 * @Description: 实现类覆盖get方法, 动态生成
 * @author: zhaozhiwei
 * @date: 2023/3/16 下午3:08
 * @version: V1.0
 */
public class AbstractGeneratorTemplateImpl implements Generator{

    @Autowired
    private AppConfiguration appConfiguration;

    public String getTemplatePath() {
        return null;
    }

    public Map<String, Object> getTemplateData(Map<String, Object> table, List<Map<String, Object>> columns) {
        return null;
    }

    @Override
    public String generatorCode(Map<String, Object> table, List<Map<String, Object>> columns) {
        Map<String, Object> map = new HashMap<>();
        map.put("author", "zhaozhiwei");
        map.put("email", "zhaozhiweishanxi@gmail.com");
        map.put("datetime", Instant.now());
        map.put("rootPath", appConfiguration.getGenerator().getBasePackage());
        final Map<String, Object> templateData = getTemplateData(table, columns);
        map.putAll(templateData);
        VelocityContext context = new VelocityContext(map);
        StringWriter sw = new StringWriter();
        Template tpl = Velocity.getTemplate(getTemplatePath(), "UTF-8");
        tpl.merge(context, sw);
        final String s = sw.toString();
        IOUtils.closeQuietly(sw);
        return s;
    }

    /**
     * @data: 2023/3/16-下午4:50
     * @User: zhaozhiwei
     * @method: getFileName

     * @return: java.lang.String
     * @Description: 文件全路径 main/java/com/example
     */
    @Override
    public String getFileName(Map<String, Object> table) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        String basePackage = appConfiguration.getGenerator().getBasePackage();
        if (StringUtils.isNotBlank(basePackage)) {
            packagePath += basePackage.replace(".", File.separator) + File.separator;
        }
        return packagePath;
    }


    /**
     * 列名转换成Java属性名
     */
    public String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    public String tableToJava(String tableName) {
        final String tablePrefix = appConfiguration.getGenerator().getTablePrefix();
        if(StringUtils.isNotBlank(tablePrefix)){
            tableName = tableName.replace(tablePrefix, "");
        }
        return columnToJava(tableName);
    }
}
