package com.z.framework.common.service;

import cn.hutool.core.lang.tree.Tree;

import java.util.List;

/**
 * @Title: MenuRouterExtInterface
 * @Package com/z/framework/common/service/MenuRouterExtInterface.java
 * @Description: 各模块可根据实际需要实现该接口, 扩展静态路由
 * @author zhaozhiwei
 * @date 2023/5/28 下午6:54
 * @version V1.0
 */
public interface MenuRouterExtService {

    List<Tree<Long>> customTreeNodeList();

}