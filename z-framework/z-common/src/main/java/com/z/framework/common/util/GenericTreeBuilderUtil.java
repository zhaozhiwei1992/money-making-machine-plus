package com.z.framework.common.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Title: TreeBuilderUtil
 * @Package com/z/framework/common/util/TreeBuilderUtil.java
 * @Description: 将List<Object>数据转换为tree形式
 * 数据中必须提供, id和parentId, 并且属性中要包含children为自身集合
 * @author zhaozhiwei
 * @date 2024/7/21 下午8:50
 * @version V1.0
 */
public class GenericTreeBuilderUtil<T extends Serializable> {

    private Class<T> clazz;

    public GenericTreeBuilderUtil(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<T> buildTree(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        // 将list转换为map形式, 方便提取数据
        Map<Serializable, T> map = new LinkedHashMap<>();
        for (T item : list) {
            try {
                Field idField = clazz.getDeclaredField("id");
                Field parentIdField = clazz.getDeclaredField("parentId");
                idField.setAccessible(true);
                parentIdField.setAccessible(true);

                Serializable id = (Serializable) idField.get(item);
                map.put(id, item);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Error while accessing fields", e);
            }
        }

        List<T> rootList = new ArrayList<>();
        for (T node : map.values()) {
            Serializable parentId = null;
            try {
                Field parentIdField = clazz.getDeclaredField("parentId");
                parentIdField.setAccessible(true);
                parentId = (Serializable) parentIdField.get(node);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Error while accessing field", e);
            }

            if (parentId == null || parentId.equals(0L) || parentId.equals("0") || parentId.equals("#")) {
                // 如果parentId为0或者null,则当前节点为一级节点
                rootList.add(node);
                buildChildren(node, map);
            }
        }
        return rootList;
    }

    /**
     * @data: 2024/7/21-下午9:01
     * @User: zhaozhiwei
     * @method: buildChildren
      * @param node :
 * @param map :  所有菜单转换的map形式
     * @return: void
     * @Description: 描述
     */
    private void buildChildren(T node, Map<Serializable, T> map) {
        // 获取当前node节点下的子节点
        List<T> children = map.values().stream()
                .filter(child -> {
                    try {
                        Field parentIdField = clazz.getDeclaredField("parentId");
                        parentIdField.setAccessible(true);
                        Serializable parentId = (Serializable) parentIdField.get(child);
                        Field idField = clazz.getDeclaredField("id");
                        idField.setAccessible(true);
                        Serializable id = (Serializable) idField.get(node);
                        return parentId != null && parentId.equals(id);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException("Error while accessing field", e);
                    }
                })
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            try {
                Field childrenField = clazz.getDeclaredField("children");
                childrenField.setAccessible(true);
                childrenField.set(node, children);
                for (T child : children) {
                    buildChildren(child, map);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Error while setting children", e);
            }
        }
    }
}