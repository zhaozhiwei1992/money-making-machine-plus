package com.z.module.generator.service.gen;

import com.z.module.generator.config.AppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Title: DataGeneratorImpl
 * @Package com/z/module/generator/service/gen/DataGeneratorImpl.java
 * @Description: 测试数据生成
 * @author zhaozhiwei
 * @date 2025/4/13 12:17
 * @version V1.0
 */
@Service
public class DataGeneratorImpl extends AbstractGeneratorTemplateImpl {

    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public String getTemplatePath() {
        return "templates/data.sql.vm";
    }

    @Override
    public Map<String, Object> getTemplateData(Map<String, Object> table, List<Map<String, Object>> columns) {
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", table.get("tableName"));
        map.put("comments", table.get("comments"));

        List<String> insertSqlList = new ArrayList<>();
        Random random = new Random();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // todo 可以考虑通过AI生成数据, 提供表列信息

        for (int i = 0; i < 10; i++) {
            StringBuilder insertSql = new StringBuilder();
            StringBuilder fields = new StringBuilder();
            StringBuilder values = new StringBuilder();
            insertSql.append("insert into ").append(table.get("tableName")).append("(");

            for (Map<String, Object> column : columns) {
                final String columnName = String.valueOf(column.get("columnName"));
                final String columnType = String.valueOf(column.get("columnType"));
                Boolean isPrimaryKey = (Boolean) column.getOrDefault("isPrimaryKey", false);
                Boolean autoIncrement = "YES".equals(column.get("isAutoIncrement"));

                // 如果是自增主键则跳过
                if (autoIncrement && isPrimaryKey) continue;

                fields.append(columnName).append(",");

                // 生成模拟值逻辑
                Object mockValue = generateMockValue(columnType, i, random, sdf);
                values.append(formatValueForSQL(mockValue, columnType)).append(",");
            }
            if (!fields.isEmpty()) fields.deleteCharAt(fields.length()-1);
            if (!values.isEmpty()) values.deleteCharAt(values.length()-1);
            insertSql.append(fields).append(") VALUES (").append(values).append(");");
            insertSqlList.add(insertSql.toString());
        }

        String deleteSql = "delete from " + table.get("tableName") + " where 1=1;";
        
        map.put("deleteSql", deleteSql);
        map.put("insertSqlList", insertSqlList);
        return map;
    }

    // 生成模拟数据核心方法
    private Object generateMockValue(String columnType, int rowIndex, Random random, SimpleDateFormat sdf) {
        // 根据字段类型生成不同数据
        if (columnType.contains("char") || columnType.contains("text")) {
            return "TestData_" + rowIndex + "_" + random.nextInt(1000);
        }
        if (columnType.startsWith("int") || columnType.contains("bigint") || columnType.contains("number")) {
            return random.nextInt(100000);
        }
        if (columnType.contains("decimal") || columnType.contains("float")) {
            return Math.round(random.nextDouble() * 10000 * 100) / 100.0;
        }
        if (columnType.contains("date") || columnType.contains("time")) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, rowIndex - 5); // 生成5天前后的时间
            return sdf.format(cal.getTime());
        }
        if (columnType.contains("boolean") || columnType.contains("bit")) {
            return random.nextBoolean();
        }
        // 其他类型处理
        return "UNKNOWN_TYPE";
    }

    // SQL值格式化方法
    private String formatValueForSQL(Object value, String columnType) {
        if (value == null) return "NULL";

        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? "1" : "0";
        }
        if (columnType.contains("DATE") || columnType.contains("TIME")) {
            return "'" + value + "'";
        }
        return "'" + value.toString().replace("'", "''") + "'"; // 处理单引号转义
    }

    @Override
    public String getFileName(Map<String, Object> table) {
        String fileName =
                "main" + File.separator + "resources" + File.separator + "liquibase" + File.separator
                        + appConfiguration.getGenerator().getModule() + File.separator + "changelog" + File.separator;
        final String className = tableToJava(String.valueOf(table.get("tableName")));
        fileName += className + "_data.sql";
        return fileName;
    }
}
