package com.z.module.generator.service;

import com.z.module.generator.config.SpringUtil;
import com.z.module.generator.service.gen.Generator;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class GeneratorService {

	public byte[] generatorCode(List<Map<String, Object>> tableList, Map<String, List<Map<String, Object>>> columnMapList) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);

		for (Map<String, Object> table : tableList) {
			//生成代码
			// 1. 设置velocity资源加载器
			Properties prop = new Properties();
			prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			Velocity.init(prop);
			// 2. 遍历接口下所有bean生成代码
			final Map<String, Generator> beansOfType =
					SpringUtil.getApplicationContext().getBeansOfType(Generator.class);
			for (Map.Entry<String, Generator> generatorBeanEntry : beansOfType.entrySet()) {
				final Generator generatorService = generatorBeanEntry.getValue();
				try {
					final String code = generatorService.generatorCode(table, columnMapList.get(table.get("tableName")));
					//添加到zip
					zip.putNextEntry(new ZipEntry(generatorService.getFileName(table)));
					IOUtils.write(code, zip, "UTF-8");
					zip.closeEntry();
				} catch (IOException e) {
					throw new RuntimeException("渲染模板失败, bean: " + generatorService, e);
				}
			}
		}
		IOUtils.closeQuietly(zip);
		return outputStream.toByteArray();
	}
}
