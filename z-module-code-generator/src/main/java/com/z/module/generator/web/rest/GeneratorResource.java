package com.z.module.generator.web.rest;

import com.z.module.generator.service.GeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Title: GeneratorController
 * @Package com/longtu/web/controller/GeneratorController.java
 * @Description: 代码生成入口
 * @author zhaozhiwei
 * @date 2023/3/16 下午2:06
 * @version V1.0
 */
@RestController
@RequestMapping("")
public class GeneratorResource {
	@Autowired
	private GeneratorService generatorService;

	/**
	 * 生成代码
	 * 前端列表配置
	 * 自己构建json, curl
	 */
	@PostMapping("/generator/code")
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
}
