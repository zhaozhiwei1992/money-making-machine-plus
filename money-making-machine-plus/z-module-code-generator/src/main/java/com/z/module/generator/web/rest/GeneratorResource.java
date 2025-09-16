package com.z.module.generator.web.rest;

import com.z.module.generator.config.SpringUtil;
import com.z.module.generator.service.GeneratorService;
import com.z.module.generator.service.gen.Generator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
 * @Title: GeneratorController
 * @Package com/longtu/web/controller/GeneratorController.java
 * @Description: 代码生成入口
 * @author zhaozhiwei
 * @date 2023/3/16 下午2:06
 * @version V1.0
 */
@RestController
@RequestMapping("generator")
@Slf4j
public class GeneratorResource {

	private final GeneratorService generatorService;

    public GeneratorResource(GeneratorService generatorService, DataSource dataSource) {
        this.generatorService = generatorService;
        this.dataSource = dataSource;
    }

    /**
	 * 生成代码
	 * 前端列表配置
	 * 自己构建json, curl
	 */
	@PostMapping("/code")
	public ResponseEntity<byte[]> code(@RequestBody Map<String, List<Map<String, Object>>> dataMap) throws IOException{

		// 1. 读取定义文件, 如: gen.org, 将定义信息转换成map表示
		final List<Map<String, Object>> tableList = new ArrayList<>();
		// key为表名
		final Map<String, List<Map<String, Object>>> columnMapList = new HashMap();

		// 将dataMap数据转换成生成需要的格式
		for (Map<String, Object> tableData : dataMap.get("tableData")) {
			final Map<String, Object> map = new HashMap<>();
			map.put("tableName", String.valueOf(tableData.get("tableName")));
			map.put("comments", String.valueOf(tableData.get("tableComments")));
			// 默认主键都是id
			map.put("pKey", "id");
			tableList.add(map);
		}

		final Map<Object, List<Map<String, Object>>> columnListGroupByTableName =
				dataMap.get("colData").stream().collect(Collectors.groupingBy(m -> m.get("tableName")));
		for (Map.Entry<Object, List<Map<String, Object>>> entry : columnListGroupByTableName.entrySet()) {
			final String tableName = String.valueOf(entry.getKey());
			final List<Map<String, Object>> columnList = entry.getValue();

			final List<Map<String, Object>> columnList2 = new ArrayList<>();
			for (Map<String, Object> colData : columnList) {
				final Map<String, Object> map = new HashMap<>();
				map.put("columnName", String.valueOf(colData.get("columnName")));
				map.put("comments", String.valueOf(colData.get("colComments")));
				map.put("columnType", String.valueOf(colData.get("columnType")));
				if("true".equals(String.valueOf(colData.get("pKey")))){
					for (Map<String, Object> tableMap : tableList) {
						tableMap.put("pKey", map.get("columnName"));
					}
				}
				columnList2.add(map);
			}
			columnMapList.put(tableName, columnList2);
		}

		// 返回文件流, 前端responseType==blob, 前端控制下载
		return ResponseEntity.ok(generatorService.generatorCode(tableList, columnMapList));
	}

	@Autowired
	private final DataSource dataSource; // 注入数据源

	@GetMapping("/tables")
	public List<HashMap<String, String>> getAllTables(String tableName) {
		List<HashMap<String, String>> tables = new ArrayList<>();

		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();

			// 获取所有表信息（参数说明：catalog, schema, tableNamePattern, types）
			try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE", "VIEW"})) {
				while (rs.next()) {
					String tableName1 = rs.getString("TABLE_NAME");
					if(tableName1.startsWith(tableName)){
						HashMap<String, String> tableInfo = new HashMap<>();
						tableInfo.put("tableName", rs.getString("TABLE_NAME"));
						tableInfo.put("tableType", rs.getString("TABLE_TYPE"));
						tableInfo.put("tableComments", rs.getString("REMARKS")); // 表注释
						tables.add(tableInfo);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch tables", e);
		}
		return tables;
	}

	@GetMapping("/cols")
	public List<Map<String, Object>> getAllCols(String tableName) {
		List<Map<String, Object>> columns = new ArrayList<>();

		try (Connection connection = dataSource.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();

			// 获取所有表信息（参数说明：catalog, schema, tableNamePattern, types）
			try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE", "VIEW"})) {
				while (rs.next()) {
					if(tableName.equals(rs.getString("TABLE_NAME"))){
						try (ResultSet columnRs = metaData.getColumns(null, null, tableName, "%")) {
							while (columnRs.next()) {
								Map<String, Object> columnInfo = new HashMap<>();
								columnInfo.put("tableName", rs.getString("TABLE_NAME"));
								columnInfo.put("columnName", columnRs.getString("COLUMN_NAME"));
								columnInfo.put("columnType", columnRs.getString("TYPE_NAME").toLowerCase());
								columnInfo.put("colComments", columnRs.getString("REMARKS"));
								columnInfo.put("columnSize", columnRs.getString("COLUMN_SIZE"));
								columnInfo.put("requirement", columnRs.getString("IS_NULLABLE").equals("YES")? "true": "false");
								columnInfo.put("pKey", columnRs.getString("COLUMN_NAME").equals("id")? "true": "false");
								columns.add(columnInfo);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch tables", e);
		}

		return columns;
	}

    @GetMapping("/code/view")
    public List<Map<String, Object>> previewCode(@RequestParam String tableName) {
        // 1. 获取表信息
        Map<String, Object> table = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            // 获取所有表信息（参数说明：catalog, schema, tableNamePattern, types）
            try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE", "VIEW"})) {
                while (rs.next()) {
                    String tableName1 = rs.getString("TABLE_NAME");
                    if(tableName1.startsWith(tableName)){
                        table.put("tableName", rs.getString("TABLE_NAME"));
                        table.put("tableType", rs.getString("TABLE_TYPE"));
                        table.put("comments", rs.getString("REMARKS")); // 表注释
                        table.put("pKey", "id");
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch tables", e);
        }
        // 2. 获取列信息
        List<Map<String, Object>> columnList = this.getAllCols(tableName);

        List<Map<String, Object>> result = new ArrayList<>();
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        // 2. 遍历接口下所有bean生成代码
        final Map<String, Generator> beansOfType =
                SpringUtil.getApplicationContext().getBeansOfType(Generator.class);
        for (Map.Entry<String, Generator> generatorBeanEntry : beansOfType.entrySet()) {
            final Generator generatorService = generatorBeanEntry.getValue();
            Map<String, Object> m = new HashMap<>();
            m.put("code", generatorService.generatorCode(table, columnList));
            m.put("filePath", generatorService.getFileName(table));
            result.add(m);
        }
        return result;
    }
}
