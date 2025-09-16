package com.z.module.generator.service.gen;

import java.util.List;
import java.util.Map;

/**
 * @Title: null.java
 * @Package: com.longtu.web.service
 * @Description: 根据表列信息生成模板字符串
 * 实现bean根据配置动态初始化
 *
 * 生成模板分类: 前后端分离: 2, 纯后端: 1
 * @author: zhaozhiwei
 * @date: 2023/3/16 下午2:21
 * @version: V1.0
 */
public interface Generator {
    String generatorCode(Map<String, Object> table, List<Map<String, Object>> columns);

    String getFileName(Map<String, Object> table);
}
