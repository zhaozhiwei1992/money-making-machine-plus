package com.z.framework.common.service;

import cn.hutool.db.meta.MetaUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseMetaService {

    private final DataSource dataSource;

    public DatabaseMetaService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private List<String> tables;

    private final Map<String, List> tableColumnsMap = new HashMap<>();

    public List<String> getTables() {
        return tables;
    }

    public Map<String, List> getTableColumnsMap() {
        return tableColumnsMap;
    }

    @PostConstruct
    public void init() {
        // 1. 初始化所有表信息
        tables = MetaUtil.getTables(dataSource);
        // 2. 初始化所有列信息
        for (String tableName : tables) {
            List<String> columns;
            columns = Arrays.asList(MetaUtil.getColumnNames(dataSource, tableName));
            tableColumnsMap.put(tableName, columns);
        }
    }
}
