package com.z.framework.screw.web.rest;

import com.z.framework.common.service.DatabaseMetaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Title: null.java
 * @Package: com.z.framework.screw.web.rest
 * @Description: 数据库信息
 * @author: zhaozhiwei
 * @date: 2023/6/12 下午4:58
 * @version: V1.0
 */
@RestController
@RequestMapping("/infra/db-info")
public class DatabaseResource {

    private final DatabaseMetaService databaseMetaService;

    public DatabaseResource(DatabaseMetaService databaseMetaService) {
        this.databaseMetaService = databaseMetaService;
    }

    /**
     * @data: 2023/6/12-下午5:04
     * @User: zhaozhiwei
     * @method: getAllTables

     * @return: org.springframework.http.ResponseEntity<com.z.framework.common.web.rest.vm.ResponseData<java.util.List<java.lang.String>>>
     * @Description: 获取所有表信息
     */
    @GetMapping("/tables")
    public List<String> getAllTables(){
        return databaseMetaService.getTables();
    }

    /**
     * @data: 2023/6/12-下午5:06
     * @User: zhaozhiwei
     * @method: getAllColumnsByTableName
      * @param tableName :
     * @return: org.springframework.http.ResponseEntity<com.z.framework.common.web.rest.vm.ResponseData<java.util.List<java.lang.String>>>
     * @Description: 获取表对应的列信息
     */
    @GetMapping("/columns")
    public List<String> getAllColumnsByTableName(String tableName){
        final Map<String, List> tableColumnsMap = databaseMetaService.getTableColumnsMap();
        return (List<String>) tableColumnsMap.get(tableName);
    }
}
